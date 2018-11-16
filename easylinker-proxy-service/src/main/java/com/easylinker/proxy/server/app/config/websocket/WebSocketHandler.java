package com.easylinker.proxy.server.app.config.websocket;


import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.mvc.WebReturnResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketHandler extends TextWebSocketHandler {

    private Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);


    public WebSocketHandler() {
        super();
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        super.handleBinaryMessage(session, message);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("WebSocket连接成功!");
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleMessage(session, message);
        try {
            JSONObject messageJson = JSONObject.parseObject(message.getPayload());
            //在这里处理消息
            //后面在处理，留个记号


        } catch (Exception e) {
            System.out.println("JSON 解析失败");
            session.sendMessage(new TextMessage(WebReturnResult.returnTipMessage(0, "必须是JSON格式!").toJSONString()));

        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        logger.info("WebSocket断开!");
    }

    @Override
    public boolean supportsPartialMessages() {
        return super.supportsPartialMessages();
    }
}