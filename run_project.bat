@echo off
title Student Scholarship and Scheme Finder Launcher
echo ======================================================================
echo  Student Scholarship and Education Scheme Finder Launcher
echo ======================================================================

set "JAVA_HOME=d:\Government Schemes\.tools\jdk-17.0.12+7"
set "PATH=%JAVA_HOME%\bin;d:\Government Schemes\.tools\apache-maven-3.9.6\bin;%PATH%"

echo [1/3] Starting Backend (Spring Boot 3 on port 8080)...
start "Scholarship Finder - Backend" cmd /k "cd /d d:\Government Schemes\backend && java -jar target\finder-1.0.0.jar"

echo [2/3] Waiting 10 seconds for backend initialization...
timeout /t 10 /nobreak > nul

echo [3/3] Starting Frontend (React Vite on port 5173)...
start "Scholarship Finder - Frontend" cmd /k "cd /d d:\Government Schemes\frontend && npm run dev"

echo.
echo ======================================================================
echo  Both services launched! Opening web browser...
echo ======================================================================
timeout /t 3 /nobreak > nul
start http://localhost:5173

echo.
echo Application is running:
echo - Frontend:    http://localhost:5173
echo - Backend API: http://localhost:8080/api
echo - Swagger Docs:http://localhost:8080/swagger-ui.html
echo.
echo Default Logins:
echo - Admin:   admin@scholarshipfinder.gov.in / Admin@123
echo - Student: kavitha@example.com / User@123
echo.
pause
