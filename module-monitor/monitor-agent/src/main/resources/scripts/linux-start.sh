#!/bin/sh
# java服务启动脚本

# 设置启动相关参数
SERVICE_NAME={}
SERVICE_HOME={}
PACKAGE_PATH={}
SERVICE_LOG_PATH={}

# 部署所用配置文件
ACTIVE_PROFILE={}

# 用于切换不同版本的java，或者指定java运行环境
JAVA_HOME={}

# 如果服务已经启动加载，则返回，避免同时启动多个服务
echo "check the same service is exist or not."
PID=$(ps --no-heading -C java -f --width 1000 | grep $SERVICE_NAME | awk '{print $2}' | head -n 1)
if [ -z "$PID" ]; then
    echo "ERROR: The $SERVICE_NAME already started! PID: $PID"
    exit 1
fi

JAVA_OPTS="-Dproject.dir=$SERVICE_HOME -server -Xmx1024m -Xms1024m -Xmn256m -XX:PermSize=128m -Xss 256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70"

echo "run java command to startup service: $SERVICE_HOME"
nohup java $JAVA_OPTS -jar $PACKAGE_PATH > $SERVICE_LOG_PATH 2>&1 &
