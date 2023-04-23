#!/bin/sh

# java服务停止脚本
SERVICE_NAME={}

# 根据服务名查找进程ID
PID=$(ps -ef | grep $SERVICE_NAME | awk '{print $2}' | head -n 1)
if [ -z "$PID" ]; then
  echo "WARNING: The $SERVICE_NAME does not started!"
  exit 0
fi

echo "Find server [$SERVICE_NAME] match PID is: $PID. Stopping..."

kill -9 $PID

echo "Stopped server [$SERVICE_NAME] Done."
