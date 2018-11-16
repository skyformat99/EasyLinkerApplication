package com.easylinker.proxy.server.app.config.websocket;


import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.mvc.WebReturnResult;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketHandler extends TextWebSocketHandler {

    private Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    private final JmsTemplate jmsTemplate;

    @Autowired
    public WebSocketHandler(JmsTemplate jmsTemplate) {
        super();
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        super.handleBinaryMessage(session, message);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("WebSocket连接成功!");
        session.sendMessage(new TextMessage(WebReturnResult.returnTipMessage(1, "连接成功!").toJSONString()));

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
    }

    /**
     * {
     * "topic" : "/1542265776092/8f4695c4e27e411abe2c04fcc92a3deb/test",
     * "message":"HelloWorld"
     * }
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            JSONObject messageJson = JSONObject.parseObject(message.getPayload());
            //在这里处理消息
            //后面在处理，留个记号
            String topic = messageJson.getString("topic");
            String sendMessage = messageJson.getString("message");
            if (topic != null && topic.length() > 0 && sendMessage != null && sendMessage.length() > 0) {
                ActiveMQTextMessage activeMQMessage = new ActiveMQTextMessage();
                activeMQMessage.setText(sendMessage);
                jmsTemplate.convertAndSend(new ActiveMQTopic(topic), activeMQMessage);
            } else {
                session.sendMessage(new TextMessage(WebReturnResult.returnTipMessage(0, "消息必须是JSON格式!").toJSONString()));

            }

        } catch (Exception e) {
            //System.out.println("JSON 解析失败");
            session.sendMessage(new TextMessage(WebReturnResult.returnTipMessage(0, "消息必须是JSON格式!").toJSONString()));

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