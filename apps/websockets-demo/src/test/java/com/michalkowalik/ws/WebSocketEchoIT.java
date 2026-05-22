package com.michalkowalik.ws;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.net.URI;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketEchoIT {

    @LocalServerPort
    int port;

    @Test
    void echo_shouldReturnPrefixedMessage() {
        var client = new ReactorNettyWebSocketClient();
        var uri = URI.create("ws://localhost:" + port + "/ws/echo");

        // Sink to capture exactly one response message
        Sinks.One<String> reply = Sinks.one();

        Mono<Void> sessionMono = client.execute(uri, session ->
                // 1) send one message
                session.send(Mono.just(session.textMessage("hello from test")))
                        // 2) then receive, map to text, take the first, push into sink
                        .thenMany(session.receive()
                                .map(WebSocketMessage::getPayloadAsText)
                                .take(1)
                                .doOnNext(msg -> reply.tryEmitValue(msg))
                        )
                        // 3) handler must return Mono<Void>
                        .then()
        );

        // Run the session and wait for completion
        sessionMono.block(Duration.ofSeconds(5));

        // Now get the captured reply
        String response = reply.asMono().block(Duration.ofSeconds(5));
        assertEquals("echo: hello from test", response);
    }
}
