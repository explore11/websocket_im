package com.song.websocket_im.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.song.websocket_im.pojo.Message;
import com.song.websocket_im.pojo.UserData;
import com.song.websocket_im.service.MessageService;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class MessageHandler extends TextWebSocketHandler implements RocketMQListener<String> {

    @Resource
    private MessageService messageService;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Map<Long, WebSocketSession> SESSIONS = new HashMap<>();


    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /* *
     * 创建连接之后
     * @param session
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long uid = (Long) session.getAttributes().get("uid");
        // 将当前用户的session放置到map中，后面会使用相应的session通信
        SESSIONS.put(uid, session);
    }

    /* *
     * 处理消息
     * @param session
     * @param textMessage
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        Long uid = (Long) session.getAttributes().get("uid");

        JsonNode jsonNode = MAPPER.readTree(textMessage.getPayload());
        Long toId = jsonNode.get("toId").asLong();
        String msg = jsonNode.get("msg").asText();

        Message message = Message.builder()
                .from(UserData.USER_MAP.get(uid))
                .to(UserData.USER_MAP.get(toId))
                .msg(msg)
                .build();

        // 将消息保存到MongoDB
        message = this.messageService.saveMessage(message);
        String msgJson = MAPPER.writeValueAsString(message);
        // 判断to用户是否在线
        WebSocketSession toSession = SESSIONS.get(toId);
        if (toSession != null && toSession.isOpen()) {
            //具体格式需要和前端对接
            toSession.sendMessage(new TextMessage(msgJson));
            // 更新消息状态为已读
            this.messageService.updateMessageState(message.getId(), 2);
        } else {
            // 该用户可能下线，可能在其他的节点中，发送消息到MQ系统
            // 需求：添加tag，便于消费者对消息的筛选
            this.rocketMQTemplate.convertAndSend("haoke-im-send-message-topic:SEND_MSG", msgJson);
        }
    }


    /* *
     * 关闭连接之后
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long uid = (Long) session.getAttributes().get("uid");
        SESSIONS.remove(uid);
        super.afterConnectionClosed(session, status);
    }


    /* *
     * 消息监听
     * @param s
     */
    @Override
    public void onMessage(String msg) {
        try {
            JsonNode jsonNode = MAPPER.readTree(msg);
            long toId = jsonNode.get("to").get("id").longValue();

            // 判断to用户是否在线
            WebSocketSession toSession = SESSIONS.get(toId);
            if (toSession != null && toSession.isOpen()) {
                //TODO 具体格式需要和前端对接
                toSession.sendMessage(new TextMessage(msg));
                // 更新消息状态为已读
                this.messageService.updateMessageState(new ObjectId(jsonNode.get("id").asText()), 2);
            } else {
                // 不需要做处理
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
