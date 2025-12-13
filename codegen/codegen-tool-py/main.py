#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
FastAPI应用入口
实现两个接口：
1. GET / - 返回helloWorld
2. POST /generate - 调用codegen_runner.py生成代码
"""

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import subprocess
import json
import tempfile
import os
from pathlib import Path

# 创建FastAPI应用
app = FastAPI(
    title="Code Generator API",
    description="代码生成器API，用于生成Java代码",
    version="1.0.0"
)

# 根路径GET请求 - 返回helloWorld
@app.get("/")
async def root():
    return {"message": "helloWorld"}

# 请求体模型
class GenerateRequest(BaseModel):
    config: dict

# POST /generate接口 - 调用codegen_runner.py
@app.post("/generate")
async def generate_code(request: GenerateRequest):
    try:
        # 创建临时配置文件 - 显式指定UTF-8编码
        with tempfile.NamedTemporaryFile(mode='w', suffix='.json', delete=False, encoding='utf-8') as f:
            json.dump(request.config, f, ensure_ascii=False, indent=2)
            temp_config_path = f.name
        
        try:
            # 获取codegen_runner.py的路径
            runner_path = Path(__file__).parent / "codegen_runner.py"
            
            # 调用codegen_runner.py
            result = subprocess.run(
                ["python", str(runner_path), temp_config_path],
                capture_output=True,
                text=True,
                encoding="utf-8"
            )
            
            # 检查执行结果
            if result.returncode == 0:
                return {
                    "status": "success",
                    "message": "代码生成完成",
                    "output": result.stdout
                }
            else:
                raise HTTPException(
                    status_code=500,
                    detail={
                        "status": "error",
                        "message": "代码生成失败",
                        "error": result.stderr,
                        "output": result.stdout
                    }
                )
        finally:
            # 清理临时文件
            if os.path.exists(temp_config_path):
                os.remove(temp_config_path)
    
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail={
                "status": "error",
                "message": f"执行出错: {str(e)}"
            }
        )

# 运行uvicorn服务器
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True
    )
