@echo off
SETLOCAL ENABLEEXTENSIONS

REM ================================
REM Code Generator Batch Script
REM ================================

SET "PYTHON=python"
SET "BASE_DIR=%~dp0"
SET "SCRIPT=%BASE_DIR%codegen-tool-py\codegen_runner.py"

REM Check Python availability
%PYTHON% --version >nul 2>&1
IF ERRORLEVEL 1 (
    echo Error: Python not found. Please install Python and add it to PATH.
    pause
    EXIT /B 1
)

REM Check script existence
IF NOT EXIST "%SCRIPT%" (
    echo Error: codegen_runner.py not found in "%BASE_DIR%codegen-tool-py".
    echo Please make sure the file exists.
    pause
    EXIT /B 1
)

REM Run generator
IF "%~1" NEQ "" (
    %PYTHON% "%SCRIPT%" "%~1"
) ELSE (
    %PYTHON% "%SCRIPT%"
)

echo.
echo Code generation completed!
pause
ENDLOCAL
