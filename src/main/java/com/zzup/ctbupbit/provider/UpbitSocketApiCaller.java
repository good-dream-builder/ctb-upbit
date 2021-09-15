package com.zzup.ctbupbit.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

//@Component
public class UpbitSocketApiCaller {
    public static Logger logger = LoggerFactory.getLogger(UpbitSocketApiCaller.class);

    void logic() {

        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketConnectionManager connectionManager = new WebSocketConnectionManager(
                webSocketClient,
                new WebSocketHandler() {
                    @Override
                    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                        logger.debug("afterConnectionEstablished");
                        session.sendMessage(new TextMessage("[{\"ticket\":\"test\"},{\"type\":\"ticker\",\"codes\":[\"KRW-BTC\"]}]"));

//                        logger.debug(session.getUri().getPath());
                    }

                    @Override
                    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                        logger.debug("handleMessage");
                        ByteBuffer byteBuffer = (ByteBuffer) message.getPayload();
                        String s = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                        logger.debug(s);
                    }

                    @Override
                    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                        logger.debug("handleTransportError");

                    }

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                        logger.debug("afterConnectionClosed");

                    }

                    @Override
                    public boolean supportsPartialMessages() {
                        logger.debug("supportsPartialMessages");

                        return false;
                    }
                },
                "wss://api.upbit.com/websocket/v1"
        );

        connectionManager.start();
        while (connectionManager.isRunning()) {

        }
    }
}
