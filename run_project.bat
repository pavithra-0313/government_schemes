@echo off
title Student Scholarship and Scheme Finder Launcher
echo ======================================================================
echo  Student Scholarship and Education Scheme Finder Launcher
echo ======================================================================

set "PROJECT_DIR=%~dp0"
set "JAVA_EXE=%PROJECT_DIR%.tools\jdk-17.0.12+7\bin\java.exe"
set "MAVEN_BIN=%PROJECT_DIR%.tools\apache-maven-3.9.6\bin"
set "PATH=%PROJECT_DIR%.tools\jdk-17.0.12+7\bin;%MAVEN_BIN%;%PATH%"

echo.
echo [1/4] Checking and freeing ports 8080 and 5173...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080 " ^| findstr "LISTENING"') do (
    echo Freeing port 8080 (PID: %%a)...
    taskkill /f /pid %%a >nul 2>&1
)
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":5173 " ^| findstr "LISTENING"') do (
    echo Freeing port 5173 (PID: %%a)...
    taskkill /f /pid %%a >nul 2>&1
)

echo.
echo [2/4] Starting Spring Boot Backend on http://localhost:8080 ...
start "Scholarship Finder - Backend (Port 8080)" cmd /k "cd /d "%PROJECT_DIR%backend" && "%JAVA_EXE%" -jar target\finder-1.0.0.jar"

echo.
echo [3/4] Waiting for Backend to be ready...
:WAIT_BACKEND
powershell -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:8080/api-docs' -UseBasicParsing -TimeoutSec 2; exit 0 } catch { exit 1 }" >nul 2>&1
if errorlevel 1 (
    echo   Initializing Spring Boot database and services...
    timeout /t 3 /nobreak > nul
    goto WAIT_BACKEND
)
echo   Backend is UP and responding on port 8080!

echo.
echo [4/4] Starting React Frontend on http://localhost:5173 ...
start "Scholarship Finder - Frontend (Port 5173)" cmd /k "cd /d "%PROJECT_DIR%frontend" && npm run dev"

timeout /t 3 /nobreak > nul
start http://localhost:5173

echo.
echo ======================================================================
echo  Student Scholarship and Scheme Finder is READY!
echo ======================================================================
echo - Frontend:    http://localhost:5173
echo - Backend API: http://localhost:8080/api
echo - Swagger Docs:http://localhost:8080/swagger-ui.html
echo.
echo Default Logins:
echo - Admin:   admin@scholarshipfinder.gov.in / Admin@123
echo - Student: kavitha@example.com / User@123
echo.
echo Keep the backend and frontend command windows open while using the app.
echo ======================================================================
pause
