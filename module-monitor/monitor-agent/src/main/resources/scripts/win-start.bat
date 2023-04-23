@echo off
:: java服务启动脚本

:: 设置启动相关参数
echo "begin set variable."
set SERVICE_NAME={}
set SERVICE_HOME={}
set PACKAGE_PATH={}
set SERVICE_LOG_PATH={}

:: 部署所用配置文件
set ACTIVE_PROFILE={}

:: 用于切换不同版本的java，或者指定java运行环境
set JAVA_HOME={}

:: 如果服务已经启动加载，则返回，避免同时启动多个服务
echo "check the same name service is exist or not."
tasklist | find "%SERVICE_NAME%"
if not errorlevel 1 (
    echo "ERROR: The %SERVICE_NAME% already started!"
    exit 1
)

:: 检查和创建日志目录
if not exist %LOG_DIR% md %LOG_DIR%

set JAVA_OPTS="-Dproject.dir=%SERVICE_HOME% -server -Xmx1024m -Xms1024m -Xmn256m -XX:PermSize=128m -Xss 256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70"

echo "run java startup command."
java %JAVA_OPTS% -jar %PACKAGE_PATH% > %SERVICE_LOG_PATH% 2>&1 &
