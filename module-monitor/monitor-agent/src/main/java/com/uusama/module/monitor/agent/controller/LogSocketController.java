package com.uusama.module.monitor.agent.controller;

import org.springframework.stereotype.Controller;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author zhaohai
 * 实时 websocket 输出 tail -f 日志
 */
@Controller
@ServerEndpoint("/api/v1/socket-logs")
public class LogSocketController {
    private Process process;
    private InputStream inputStream;

    /**
     * 新的WebSocket请求开启
     */
    @OnOpen
    public void onOpen(Session session) {
        try {
            // 执行tail -f命令
            process = Runtime.getRuntime().exec("tail -f /opt/csdn.log");
            inputStream = process.getInputStream();

            // 一定要启动新的线程，防止InputStream阻塞处理WebSocket的线程
            new Thread(() -> {
                String line;
                try {
                    while ((line = new BufferedReader(new InputStreamReader(inputStream)).readLine()) != null) {
                        // 将实时日志通过WebSocket发送给客户端，给每一行添加一个HTML换行
                        session.getBasicRemote().sendText(line + "<br>");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * WebSocket请求关闭
     */
    @OnClose
    public void onClose() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (process != null) {
            process.destroy();
        }
    }

    @OnError
    public void onError(Throwable thr) {
        thr.printStackTrace();
    }
}
