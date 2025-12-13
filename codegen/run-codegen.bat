@echo off
SETLOCAL

REM Code Generator Batch Script
SET PYTHON=python
SET SCRIPT=%~dp0codegen_runner.py

REM Check Python availability
%PYTHON% --version >nul 2>&1
if errorlevel 1 (
    echo Error: Python not found. Please install Python and add to PATH.
    pause
    exit /b 1
)

REM Check script existence
if not exist "%SCRIPT%" (
    echo Error: codegen_runner.py not found.
    pause
    exit /b 1
)

REM Run generator
if "%~1" neq "" (
    %PYTHON% "%SCRIPT%" "%~1"
) else (
    %PYTHON% "%SCRIPT%"
)

echo.
echo Code generation completed!
pause
ENDLOCAL
