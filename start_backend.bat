@echo off
title Scholarship Finder - Backend (Port 8080)
set "PROJECT_DIR=%~dp0"
set "JAVA_EXE=%PROJECT_DIR%.tools\jdk-17.0.12+7\bin\java.exe"
cd /d "%PROJECT_DIR%backend"

for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080 " ^| findstr "LISTENING"') do (
    echo Freeing occupied port 8080 (PID: %%a)...
    taskkill /f /pid %%a >nul 2>&1
)

echo ======================================================================
echo Starting Spring Boot Backend on http://localhost:8080 ...
echo ======================================================================
"%JAVA_EXE%" -jar target\finder-1.0.0.jar
pause
