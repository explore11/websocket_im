package com.song.websocket_im.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/* *
 * 拦截器
 */
@Component
public class MessageHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        boolean result = false;
        String path = request.getURI().getPath();
        String[] ss = StringUtils.split(path, '/');
        if (ss.length == 2) {//是否为数字
            if (StringUtils.isNumeric(ss[1])) {// 获取ws连接协议后面的参数
                attributes.put("uid", Long.valueOf(ss[1]));
                result = true;
            }
        }
        return result;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
