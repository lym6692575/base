#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
FastAPI 应用入口
实现两个接口：
1. GET / - 返回 helloWorld
2. POST /generate - 调用 codegen_runner.py 生成代码
"""

from fastapi import FastAPI, HTTPException, Path
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import subprocess
import json
import tempfile
import os
import locale
import sqlite3
from pathlib import Path as FilePath
from typing import Optional
from codegen.config_loader import ConfigLoader

# 初始化配置加载器
config_loader = ConfigLoader()

# 创建 FastAPI 应用
app = FastAPI(
    title="Code Generator API",
    description="代码生成器 API，用于生成 Java 代码",
    version="1.0.0"
)

# 配置 CORS 中间件
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 允许所有源
    allow_credentials=True,
    allow_methods=["*"],  # 允许所有方法
    allow_headers=["*"],  # 允许所有头部
    expose_headers=["*"]
)

# 根路径 GET 请求 - 返回 helloWorld
@app.get("/")
async def root():
    return {"message": "helloWorld123"}


# 请求体模型
class GenerateRequest(BaseModel):
    config: Optional[dict] = None


def decode_output(data: bytes) -> str:
    """根据常见编码尝试解码子进程输出"""
    if not data:
        return ""
    candidates = ["utf-8", locale.getpreferredencoding(False), "cp936", "gbk"]
    tried = set()
    for enc in candidates:
        if not enc:
            continue
        key = enc.lower()
        if key in tried:
            continue
        tried.add(key)
        try:
            return data.decode(enc)
        except UnicodeDecodeError:
            continue
    # 实在无法解码就替换非法字符
    return data.decode("utf-8", errors="replace")


# POST /generate 接口 - 调用 codegen_runner.py
@app.post("/generate")
async def generate_code(request: Optional[GenerateRequest] = None):
    try:
        runner_path = FilePath(__file__).parent / "codegen_runner.py"
        if not runner_path.exists():
            raise HTTPException(
                status_code=500,
                detail={
                    "status": "error",
                    "message": f"codegen_runner.py not found at: {runner_path}"
                }
            )

        # 由于不再支持从配置文件加载，该接口需要重新设计
        # 目前保留该接口，但返回错误信息，提示使用 /generate/{id} 接口
        raise HTTPException(
            status_code=400,
            detail={
                "status": "error",
                "message": "该接口已不再支持直接生成代码，请使用 /generate/{id} 接口并提供配置ID"
            }
        )

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail={
                "status": "error",
                "message": f"执行出错: {str(e)}"
            }
        )


# 配置请求模型
class ConfigRequest(BaseModel):
    config: dict


# POST /generate/{id} - 使用指定配置ID生成代码
@app.post("/generate/{id}")
async def generate_code_by_id(id: int = Path(..., description="配置ID")):
    try:
        runner_path = FilePath(__file__).parent / "codegen_runner.py"
        if not runner_path.exists():
            raise HTTPException(
                status_code=500,
                detail={
                    "status": "error",
                    "message": f"codegen_runner.py not found at: {runner_path}"
                }
            )

        # 调用子进程，使用指定配置ID
        cmd = ["python", str(runner_path), f"--config-id={id}"]
        result = subprocess.run(
            cmd,
            capture_output=True,
            text=False,  # 二进制模式
            timeout=30,
            cwd=str(runner_path.parent)
        )

        stdout = decode_output(result.stdout)
        stderr = decode_output(result.stderr)

        if result.returncode == 0:
            return {
                "status": "success",
                "message": "代码生成完成",
                "output": stdout if stdout else "无输出信息"
            }
        else:
            raise HTTPException(
                status_code=500,
                detail={
                    "status": "error",
                    "message": "代码生成失败",
                    "error": stderr if stderr else "无错误信息",
                    "output": stdout if stdout else "无输出信息",
                    "return_code": result.returncode
                }
            )
    except subprocess.TimeoutExpired:
        raise HTTPException(
            status_code=500,
            detail={
                "status": "error",
                "message": "代码生成超时",
                "error": "代码生成过程超过30秒，请检查配置和代码生成器逻辑",
                "output": ""
            }
        )
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail={
                "status": "error",
                "message": f"执行出错: {str(e)}"
            }
        )


# GET /configs - 获取所有配置列表
@app.get("/configs")
async def get_all_configs():
    try:
        configs = config_loader.get_all_configs()
        return {
            "status": "success",
            "data": configs,
            "total": len(configs)
        }
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail={
                "status": "error",
                "message": f"获取配置列表失败: {str(e)}"
            }
        )


# GET /configs/{id} - 获取特定配置
@app.get("/configs/{id}")
async def get_config(id: int = Path(..., description="配置ID")):
    try:
        config = config_loader.load_from_db(id)
        if not config:
            raise HTTPException(
                status_code=404,
                detail={
                    "status": "error",
                    "message": f"配置ID {id} 不存在"
                }
            )
        return {
            "status": "success",
            "data": config
        }
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail={
                "status": "error",
                "message": f"获取配置失败: {str(e)}"
            }
        )


# POST /configs - 创建新配置
@app.post("/configs")
async def create_config(request: ConfigRequest):
    try:
        config_id = config_loader.save_to_db(request.config)
        return {
            "status": "success",
            "message": "配置创建成功",
            "config_id": config_id
        }
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail={
                "status": "error",
                "message": f"创建配置失败: {str(e)}"
            }
        )


# PUT /configs/{id} - 更新配置
@app.put("/configs/{id}")
async def update_config(
    request: ConfigRequest,
    id: int = Path(..., description="配置ID")
):
    try:
        # 先检查配置是否存在
        existing_config = config_loader.load_from_db(id)
        if not existing_config:
            raise HTTPException(
                status_code=404,
                detail={
                    "status": "error",
                    "message": f"配置ID {id} 不存在"
                }
            )
        
        # 更新现有配置
        new_config_id = config_loader.save_to_db(request.config, id)
        
        return {
            "status": "success",
            "message": "配置更新成功",
            "config_id": new_config_id
        }
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail={
                "status": "error",
                "message": f"更新配置失败: {str(e)}"
            }
        )


# DELETE /configs/{id} - 删除配置
@app.delete("/configs/{id}")
async def delete_config(id: int = Path(..., description="配置ID")):
    try:
        db_path = config_loader.db_path
        if not db_path.exists():
            raise HTTPException(
                status_code=500,
                detail={
                    "status": "error",
                    "message": "数据库文件不存在"
                }
            )
        
        conn = sqlite3.connect(db_path)
        cursor = conn.cursor()
        
        try:
            # 开始事务
            conn.execute('BEGIN TRANSACTION')
            
            # 删除配置的字段
            cursor.execute('DELETE FROM fields WHERE config_id = ?', (id,))
            # 删除配置
            cursor.execute('DELETE FROM config WHERE id = ?', (id,))
            
            # 提交事务
            conn.commit()
            
            return {
                "status": "success",
                "message": f"配置ID {id} 删除成功"
            }
        except Exception as e:
            conn.rollback()
            raise HTTPException(
                status_code=500,
                detail={
                    "status": "error",
                    "message": f"删除配置失败: {str(e)}"
                }
            )
        finally:
            conn.close()
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail={
                "status": "error",
                "message": f"删除配置失败: {str(e)}"
            }
        )


# 运行 uvicorn 服务器
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True
    )
