package com.michalkowalik.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

public class EchoWebSocketHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(EchoWebSocketHandler.class);

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(
                session.receive().map(msg -> {
                    String in = msg.getPayloadAsText();
                    log.info("inbound: {}", in);
                    return session.textMessage("echo: " + in);
                })
        );
    }
}
