# WebSockets Demo (Spring Boot + WebFlux, Gradle KTS)

A minimal project that demonstrates **WebSockets** using Spring Boot WebFlux (reactive).  
It exposes an echo endpoint and a tiny HTML client to connect, send, and receive messages.

---

## Features

- WebSocket endpoint: `/ws/echo` (reactive echo handler)
- Static HTML client served at `/` (under `src/main/resources/static/index.html`)
- Simple echo behavior: sending `hello` yields `echo: hello`

---

## Requirements

- JDK 21
- Gradle Wrapper (`./gradlew`)
- Optional: Spring Boot DevTools for rapid development

---

## How to run

```
./gradlew :apps:websockets-demo:bootRun
```

Then open:

- Browser: `http://localhost:8080/`  
  Click **Connect**, type a message, click **Send** — you should see the echoed response.

---

## Endpoint

- **WebSocket**: `ws://localhost:8080/ws/echo`  
  Protocol upgrade to WebSocket is handled by WebFlux via `WebSocketHandlerAdapter` and `SimpleUrlHandlerMapping`.

---

## Quick CLI test (optional)

**Using wscat** (Node.js):

```
wscat -c ws://localhost:8080/ws/echo
# type a message, expect: "echo: <your message>"
```

**Using websocat** (single binary):

```
websocat ws://localhost:8080/ws/echo
```

**Handshake only (curl):**

```
curl -i -N \
-H "Connection: Upgrade" -H "Upgrade: websocket" \
-H "Host: localhost:8080" -H "Origin: http://localhost:8080" \
http://localhost:8080/ws/echo
# Expect: HTTP/1.1 101 Switching Protocols
```

---

## Tests

Integration test verifies the echo flow end-to-end:

- `src/test/java/com/example/ws/WebSocketEchoIT.java`  
  Starts the app on a random port, connects to `/ws/echo`, sends a message, and asserts the echoed reply.

Run tests:

```
./gradlew :apps:websockets-demo:test
```

---

## Notes

- If you run another app on port 8080, either stop it or set a different port in `application.yml`.
- The static page `/` is served from `src/main/resources/static/` — Spring Boot auto-detects `index.html`.
- For production-style demos, consider message size limits, authentication, and proper timeouts/heartbeats.

---
