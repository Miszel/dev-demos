# SSE Demo (Spring Boot + WebFlux, Gradle KTS)

A minimal project that demonstrates **Server-Sent Events (SSE)** with Spring Boot (WebFlux) and Gradle Kotlin DSL.

It shows two use cases:
- `GET /sse/ticks` — a **cold** per-client stream emitting a heartbeat every second.
- `GET /sse/stream` + `POST /sse/publish` — a **hot** shared stream (multicast) to broadcast application events.

---

## Requirements

- **JDK 21**
- **Gradle Wrapper** (`./gradlew`)
- (Optional) **Spring Boot DevTools** for live reload during development

---

## Run (development)

```bash
./gradlew bootRun
```

The app starts at http://localhost:8080.
Open http://localhost:8080/ to see a simple HTML page with two panels:
- **Sender** — sends events to POST /sse/publish (choose type, provide JSON payload).
- **Receiver** — connects/disconnects from GET /sse/stream and displays incoming events.

There’s also an optional panel for GET /sse/ticks.

With DevTools, changes to src/main/resources/static/index.html are picked up without restarting.
If the browser caches aggressively, do a hard refresh (Ctrl/Cmd + Shift + R).

---

## Quick test (terminal)

Open a subscriber:
```
curl -N http://localhost:8080/sse/stream
```

Publish an event:
```
curl -X POST http://localhost:8080/sse/publish \
-H 'Content-Type: application/json' \
-d '{"type":"message","text":"hello from curl"}'
```

Heartbeat (per-client stream):
```
curl -N http://localhost:8080/sse/ticks
```

---

## Endpoints

- `GET /sse/ticks` → `text/event-stream` — emits `event: tick` every second.
- `GET /sse/stream` → `text/event-stream` — shared multicast channel.
- `POST /sse/publish` (JSON) — publishes to the shared stream.
The `type` field becomes the SSE `event:` name.
The rest of the JSON is included in `data` (as JSON).

---

## Unsubscribing (client)

Use the native EventSource API:
```
const es = new EventSource('/sse/stream');
// ...
es.close(); // stops receiving and disables auto-reconnect
```
The demo page includes **Connect / Disconnect** buttons for both /sse/stream and /sse/ticks.

---

## Notes

- **CORS**: If your UI runs on a different origin, enable CORS in Spring or serve the static HTML from this app (as in the demo).
- **Proxies/Timeouts**: SSE uses long-lived connections. When running behind proxies (nginx/Envoy/etc.), verify idle/read timeouts.
- **Backpressure**: The shared stream uses a Reactor `Sinks.many().multicast().onBackpressureBuffer()`. In production, consider buffer limits and policies.

## License

Educational demo — use freely for learning purposes.