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
        
        # 1. 尝试从命令行参数获取配置文件路径
        parser = argparse.ArgumentParser(description='代码生成器')
        parser.add_argument('config_path', nargs='?', default=None, help='配置文件路径')
        args = parser.parse_args()
        config_path_arg = args.config_path
        
        # 2. 尝试从系统环境变量获取
        config_path = config_path_arg or os.environ.get('CODEGEN_CONFIG', 'codegen.properties')
        
        # 3. 如果使用的是默认配置名且文件不存在，进行智能回退
        if config_path_arg is None and config_path == 'codegen.properties' and not Path(config_path).exists():
            
            # 3.1 尝试在当前目录及其子目录中寻找 codegen.json
            current_dir = Path('.')
            found = False
            
            # 递归地搜索当前目录及其子目录，寻找 codegen.json 文件
            # 限制递归搜索的深度为 3，避免搜索过深导致性能问题
            for file in current_dir.rglob('codegen.json'):
                if file.is_file():
                    print(f"检测到配置文件: {file.absolute()}")
                    config_path = str(file.absolute())
                    found = True
                    break
            
            # 3.2 若自动探测失败，则要求用户手动输入
            if not found:
                print("未找到默认配置文件 (codegen.properties/json)，请手动输入配置文件路径:")
                input_path = input().strip()
                if not input_path or not Path(input_path).exists():
                    print("无效的配置文件路径，已取消生成")
                    sys.exit(1)
                config_path = input_path
        
        # 4. 加载配置并执行生成
        print(f"正在加载配置: {config_path}")
        
        # 加载配置
        config_loader = ConfigLoader()
        config_dict = config_loader.load(config_path)
        
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
