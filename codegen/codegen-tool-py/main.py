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


# GET /schemes - 获取所有方案
@app.get("/schemes")
async def get_all_schemes():
    try:
        db_path = config_loader.db_path
        conn = sqlite3.connect(db_path)
        conn.row_factory = sqlite3.Row
        cursor = conn.cursor()
        
        cursor.execute("SELECT * FROM schemes ORDER BY id")
        schemes = [dict(row) for row in cursor.fetchall()]
        
        conn.close()
        
        return {
            "status": "success",
            "data": schemes
        }
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail={
                "status": "error",
                "message": f"获取方案列表失败: {str(e)}"
            }
        )

# GET /templates - 获取所有模板
@app.get("/templates")
async def get_all_templates():
    try:
        db_path = config_loader.db_path
        conn = sqlite3.connect(db_path)
        conn.row_factory = sqlite3.Row
        cursor = conn.cursor()
        
        # 只查询未删除的模板
        cursor.execute("SELECT id, name, description, type, updated_at FROM templates WHERE is_deleted = 0 ORDER BY name")
        templates = [dict(row) for row in cursor.fetchall()]
        
        conn.close()
        
        return {
            "status": "success",
            "data": templates
        }
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail={
                "status": "error",
                "message": f"获取模板列表失败: {str(e)}"
            }
        )

# GET /templates/{id} - 获取特定模板内容
@app.get("/templates/{id}")
async def get_template(id: int):
    try:
        db_path = config_loader.db_path
        conn = sqlite3.connect(db_path)
        conn.row_factory = sqlite3.Row
        cursor = conn.cursor()
        
        cursor.execute("SELECT * FROM templates WHERE id = ? AND is_deleted = 0", (id,))
        row = cursor.fetchone()
        conn.close()
        
        if not row:
            raise HTTPException(status_code=404, detail="Template not found")
            
        return {
            "status": "success",
            "data": dict(row)
        }
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail={
                "status": "error",
                "message": f"获取模板内容失败: {str(e)}"
            }
        )

# PUT /templates/{id} - 更新模板内容
@app.put("/templates/{id}")
async def update_template(id: int, request: dict):
    try:
        content = request.get('content')
        type_ = request.get('type')
        description = request.get('description')
        
        # Build query dynamically
        updates = ["updated_at = CURRENT_TIMESTAMP"]
        params = []
        
        if content is not None:
            updates.append("content = ?")
            params.append(content)
        if type_ is not None:
            updates.append("type = ?")
            params.append(type_)
        if description is not None:
            updates.append("description = ?")
            params.append(description)
            
        params.append(id)
        
        db_path = config_loader.db_path
        conn = sqlite3.connect(db_path)
        cursor = conn.cursor()
        
        cursor.execute(
            f"UPDATE templates SET {', '.join(updates)} WHERE id = ?",
            params
        )
        conn.commit()
        conn.close()
        
        return {"status": "success", "message": "Template updated"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# POST /templates - 创建新模板
@app.post("/templates")
async def create_template(request: dict):
    try:
        name = request.get('name')
        content = request.get('content', '')
        description = request.get('description', '')
        type_ = request.get('type', '')
        
        if not name:
             raise HTTPException(status_code=400, detail="Name is required")

        db_path = config_loader.db_path
        conn = sqlite3.connect(db_path)
        cursor = conn.cursor()
        
        try:
            cursor.execute(
                "INSERT INTO templates (name, content, description, type) VALUES (?, ?, ?, ?)",
                (name, content, description, type_)
            )
            new_id = cursor.lastrowid
            conn.commit()
            return {"status": "success", "data": {"id": new_id}, "message": "Template created"}
        except sqlite3.IntegrityError:
            raise HTTPException(status_code=409, detail="Template name already exists")
        finally:
            conn.close()
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# DELETE /templates/{id} - 标记删除模板
@app.delete("/templates/{id}")
async def delete_template(id: int):
    try:
        db_path = config_loader.db_path
        conn = sqlite3.connect(db_path)
        cursor = conn.cursor()
        
        cursor.execute(
            "UPDATE templates SET is_deleted = 1, updated_at = CURRENT_TIMESTAMP WHERE id = ?",
            (id,)
        )
        conn.commit()
        conn.close()
        
        return {"status": "success", "message": "Template deleted"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# --- Scheme APIs ---

# POST /schemes - 创建方案
@app.post("/schemes")
async def create_scheme(request: dict):
    try:
        name = request.get('name')
        description = request.get('description', '')
        group_name = request.get('group_name', 'Default')
        variables = request.get('variables', '{}') # JSON string
        
        if not name:
             raise HTTPException(status_code=400, detail="Name is required")

        db_path = config_loader.db_path
        conn = sqlite3.connect(db_path)
        cursor = conn.cursor()
        
        try:
            cursor.execute(
                "INSERT INTO schemes (name, description, group_name, variables) VALUES (?, ?, ?, ?)",
                (name, description, group_name, variables)
            )
            new_id = cursor.lastrowid
            conn.commit()
            return {"status": "success", "data": {"id": new_id}, "message": "Scheme created"}
        except sqlite3.IntegrityError:
            raise HTTPException(status_code=409, detail="Scheme name already exists")
        finally:
            conn.close()
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# PUT /schemes/{id} - 更新方案
@app.put("/schemes/{id}")
async def update_scheme(id: int, request: dict):
    try:
        name = request.get('name')
        description = request.get('description')
        group_name = request.get('group_name')
        variables = request.get('variables') # Expecting JSON string or dict
        
        # If variables is a dict, convert to string
        if isinstance(variables, dict):
            variables = json.dumps(variables, ensure_ascii=False)

        db_path = config_loader.db_path
        conn = sqlite3.connect(db_path)
        cursor = conn.cursor()
        
        updates = []
        params = []
        
        if name is not None:
            updates.append("name = ?")
            params.append(name)
        if description is not None:
            updates.append("description = ?")
            params.append(description)
        if group_name is not None:
            updates.append("group_name = ?")
            params.append(group_name)
        if variables is not None:
            updates.append("variables = ?")
            params.append(variables)
            
        if not updates:
            return {"status": "success", "message": "Nothing to update"}
            
        params.append(id)
        
        try:
            cursor.execute(f"UPDATE schemes SET {', '.join(updates)} WHERE id = ?", params)
            conn.commit()
            return {"status": "success", "message": "Scheme updated"}
        except sqlite3.IntegrityError:
            raise HTTPException(status_code=409, detail="Scheme name already exists")
        finally:
            conn.close()
            
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# DELETE /schemes/{id} - 删除方案
@app.delete("/schemes/{id}")
async def delete_scheme(id: int):
    try:
        db_path = config_loader.db_path
        conn = sqlite3.connect(db_path)
        cursor = conn.cursor()
        
        # 先删除关联的 items
        cursor.execute("DELETE FROM scheme_items WHERE scheme_id = ?", (id,))
        # 再删除 scheme
        cursor.execute("DELETE FROM schemes WHERE id = ?", (id,))
        
        conn.commit()
        conn.close()
        
        return {"status": "success", "message": "Scheme deleted"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# GET /schemes/{id}/items - 获取方案的所有步骤
@app.get("/schemes/{id}/items")
async def get_scheme_items(id: int):
    try:
        db_path = config_loader.db_path
        conn = sqlite3.connect(db_path)
        conn.row_factory = sqlite3.Row
        cursor = conn.cursor()
        
        # 关联查询模板名称
        query = """
        SELECT si.*, t.name as template_name 
        FROM scheme_items si
        LEFT JOIN templates t ON si.template_id = t.id
        WHERE si.scheme_id = ?
        ORDER BY si.id
        """
        cursor.execute(query, (id,))
        items = [dict(row) for row in cursor.fetchall()]
        
        conn.close()
        
        return {
            "status": "success",
            "data": items
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# POST /scheme_items - 添加步骤
@app.post("/scheme_items")
async def create_scheme_item(request: dict):
    try:
        scheme_id = request.get('scheme_id')
        template_id = request.get('template_id')
        output_filename_pattern = request.get('output_filename_pattern')
        output_sub_package = request.get('output_sub_package')
        
        if not all([scheme_id, template_id, output_filename_pattern, output_sub_package]):
             raise HTTPException(status_code=400, detail="Missing required fields")

        db_path = config_loader.db_path
        conn = sqlite3.connect(db_path)
        cursor = conn.cursor()
        
        cursor.execute(
            """
            INSERT INTO scheme_items (scheme_id, template_id, output_filename_pattern, output_sub_package, is_enabled) 
            VALUES (?, ?, ?, ?, 1)
            """,
            (scheme_id, template_id, output_filename_pattern, output_sub_package)
        )
        conn.commit()
        conn.close()
        
        return {"status": "success", "message": "Item created"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# PUT /scheme_items/{id} - 更新步骤
@app.put("/scheme_items/{id}")
async def update_scheme_item(id: int, request: dict):
    try:
        output_filename_pattern = request.get('output_filename_pattern')
        output_sub_package = request.get('output_sub_package')
        is_enabled = request.get('is_enabled')
        variables = request.get('variables') # Expecting JSON string or dict
        
        # If variables is a dict, convert to string
        if isinstance(variables, dict):
            variables = json.dumps(variables, ensure_ascii=False)
        
        db_path = config_loader.db_path
        conn = sqlite3.connect(db_path)
        cursor = conn.cursor()
        
        # 动态构建更新语句
        updates = []
        params = []
        if output_filename_pattern is not None:
            updates.append("output_filename_pattern = ?")
            params.append(output_filename_pattern)
        if output_sub_package is not None:
            updates.append("output_sub_package = ?")
            params.append(output_sub_package)
        if is_enabled is not None:
            updates.append("is_enabled = ?")
            params.append(is_enabled)
        if variables is not None:
            updates.append("variables = ?")
            params.append(variables)
            
        if not updates:
             return {"status": "success", "message": "Nothing to update"}
             
        params.append(id)
        cursor.execute(f"UPDATE scheme_items SET {', '.join(updates)} WHERE id = ?", params)
        conn.commit()
        conn.close()
        
        return {"status": "success", "message": "Item updated"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# DELETE /scheme_items/{id} - 删除步骤
@app.delete("/scheme_items/{id}")
async def delete_scheme_item(id: int):
    try:
        db_path = config_loader.db_path
        conn = sqlite3.connect(db_path)
        cursor = conn.cursor()
        
        cursor.execute("DELETE FROM scheme_items WHERE id = ?", (id,))
        conn.commit()
        conn.close()
        
        return {"status": "success", "message": "Item deleted"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# 运行 uvicorn 服务器
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True
    )
