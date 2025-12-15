#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
代码生成器主程序

程序启动流程：
1. 优先检查命令行参数是否指定配置文件路径。
2. 若未指定，则检查系统属性 codegen.config。
3. 若均未指定，默认寻找当前目录下的 codegen.properties。
4. 若 codegen.properties 不存在，则尝试在多个常见位置寻找 codegen.json。
5. 若上述文件均不存在，则要求用户手动输入路径。
"""

import os
import sys
import argparse
from pathlib import Path
from datetime import datetime
from codegen.config_loader import ConfigLoader
from codegen.code_generator import CodeGenerator

def main():
    try:
        print(f"执行时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        
        # 1. 尝试从命令行参数获取配置ID
        parser = argparse.ArgumentParser(description='代码生成器')
        parser.add_argument('--config-id', type=int, required=True, help='配置ID，从数据库加载时使用')
        args = parser.parse_args()
        config_id = args.config_id
        
        # 2. 加载配置并执行生成
        print(f"正在从数据库加载配置，ID: {config_id}")
        
        # 加载配置
        config_loader = ConfigLoader()
        config_dict = config_loader.load(config_id=config_id)
        
        # 创建代码生成器并执行
        generator = CodeGenerator(config_dict)
        generator.generate()
        
        print(f"代码生成完成! 实体名称: {config_dict.get('entityName')}")
        if generator.main_entity_path:
            print(f"输出路径: {generator.main_entity_path.absolute()}")
            
    except Exception as e:
        print(f"\n[ERROR] 代码生成失败: {str(e)}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    main()
