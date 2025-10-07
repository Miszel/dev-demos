package com.michalkowalik;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/sse")
public class SseController {

    private static final Logger log = LoggerFactory.getLogger(SseController.class);

    private final AtomicLong idGen = new AtomicLong(1);
    private final Sinks.Many<ServerSentEvent<Map<String, Object>>> sink =
            Sinks.many().multicast().onBackpressureBuffer();

    @GetMapping(path = "/ticks", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Map<String, Object>>> ticks() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(seq -> ServerSentEvent.<Map<String, Object>>builder()
                        .id(Long.toString(idGen.getAndIncrement()))
                        .event("tick")
                        .data(Map.of(
                                "seq", seq,
                                "time", Instant.now().toString()
                        ))
                        .build());
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Map<String, Object>>> stream() {
        return sink.asFlux()
                .doOnSubscribe(s -> log.info("client connected"))
                .doOnCancel(() -> log.info("client disconnected"))
                .doFinally(sig -> log.info("stream finished: {}", sig));
    }

    @PostMapping(path = "/publish")
    public void publish(@RequestBody Map<String, Object> payload) {
        var event = ServerSentEvent.<Map<String, Object>>builder()
                .id(Long.toString(idGen.getAndIncrement()))
                .event((String) payload.getOrDefault("type", "message"))
                .data(Map.of(
                        "payload", payload,
                        "time", Instant.now().toString()
                ))
                .build();
        sink.tryEmitNext(event);
    }

}
