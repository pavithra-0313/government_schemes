@echo off
title Scholarship Finder - Frontend (Port 5173)
cd /d "%~dp0frontend"

for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":5173 " ^| findstr "LISTENING"') do (
    echo Freeing occupied port 5173 (PID: %%a)...
    taskkill /f /pid %%a >nul 2>&1
)

echo ======================================================================
echo Starting React Vite Frontend on http://localhost:5173 ...
echo ======================================================================
npm run dev
pause
