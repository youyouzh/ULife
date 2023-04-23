@echo off

:: java服务停止脚本
set SERVICE_NAME={}

:: 根据服务名查找进程ID
echo "find the same service process id."
set "PID="
for /f "tokens=2" %%A in ('tasklist ^| findstr /i "%SERVICE_NAME%" 2^>NUL') do @Set "PID=%%A"

echo "find pid: %PID%, then task kill it."
if defined PID taskkill /pid %PID%
