@echo off
setlocal EnableExtensions
chcp 65001 >nul

rem zhoudaDataCheck UI — local backend + LAN access
rem Usage:
rem   start-dev.cmd
rem   start-dev.cmd http://127.0.0.1:5600/
rem   start-dev.cmd http://192.168.1.10:5600/ 5200

cd /d "%~dp0"

set "DV_API_TARGET=http://127.0.0.1:5600/"
set "DV_UI_PORT=5100"
if not "%~1"=="" set "DV_API_TARGET=%~1"
if not "%~2"=="" set "DV_UI_PORT=%~2"

rem Ensure trailing slash on API target
if not "%DV_API_TARGET:~-1%"=="/" set "DV_API_TARGET=%DV_API_TARGET%/"

if not exist "node_modules\" (
  echo [zhoudaDataCheck] node_modules missing, running npm install...
  call npm install
  if errorlevel 1 (
    echo [zhoudaDataCheck] npm install failed.
    exit /b 1
  )
)

set "LAN_IP="
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /c:"IPv4"') do (
  for /f "tokens=*" %%b in ("%%a") do (
    echo %%b | findstr /r "^192\.168\." >nul && set "LAN_IP=%%b"
    if not defined LAN_IP echo %%b | findstr /r "^10\." >nul && set "LAN_IP=%%b"
    if not defined LAN_IP echo %%b | findstr /r "^172\." >nul && set "LAN_IP=%%b"
  )
)

echo.
echo ========== zhoudaDataCheck UI ==========
echo  Backend proxy : %DV_API_TARGET%
echo  Listen        : 0.0.0.0:%DV_UI_PORT%  (LAN enabled)
echo  Local URL     : http://127.0.0.1:%DV_UI_PORT%/
if defined LAN_IP (
  echo  LAN URL       : http://%LAN_IP%:%DV_UI_PORT%/
) else (
  echo  LAN URL       : http://^<your-lan-ip^>:%DV_UI_PORT%/
)
echo  Tip: allow inbound TCP %DV_UI_PORT% in Windows Firewall if LAN clients cannot open the page.
echo ========================================
echo.

call npm run dev:prod
endlocal
