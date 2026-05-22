# OAuth2 Authorization Code + PKCE Demo (Spring Boot, Gradle KTS)

A self-contained demo of the **OAuth2 Authorization Code Flow with PKCE** — the modern pattern for browser-based public clients (SPAs) that cannot keep a client secret.

A single Spring Boot process runs on port 8080, acting as three things at once:
- **Authorization Server** (Spring Authorization Server) — issues authorization codes and JWT access tokens
- **Resource Server** — validates JWTs and serves the protected `/api/me` endpoint
- **Static host** — serves the vanilla JS SPA that drives the entire flow from the browser

---

## How to run

```
./gradlew :apps:oauth2-demo:bootRun
```

Open `http://localhost:8080/`, click **Start OAuth2 Flow**, and log in as `user` / `password`.

---

## What to observe

The page's event log annotates each step of the protocol:

| Step | What happens |
|------|-------------|
| 1 | Browser generates `code_verifier` (random bytes) and `code_challenge = base64url(SHA-256(verifier))` |
| 2 | Browser redirects to `/oauth2/authorize` with the challenge — `code_verifier` stays in `sessionStorage`, never sent yet |
| 3 | AS shows the login form; user authenticates; AS issues an authorization code and redirects back to `/?code=...` |
| 4 | SPA POSTs to `/oauth2/token` with `code` + `code_verifier`; AS verifies the challenge, issues an access token (JWT) |
| 5 | SPA calls `GET /api/me` with `Authorization: Bearer <token>` |
| 6 | Resource server validates the JWT signature against `/oauth2/jwks`; returns `{"sub":"user",...}` |

---

## Key endpoints

| Endpoint | Purpose |
|----------|---------|
| `/oauth2/authorize` | Authorization endpoint — start the flow here |
| `/oauth2/token` | Token endpoint — exchange code for tokens |
| `/oauth2/jwks` | JSON Web Key Set — public keys for JWT verification |
| `/.well-known/openid-configuration` | OIDC discovery document |
| `/api/me` | Protected resource — requires Bearer token |

---

## Inspecting the JWT

After completing the flow, open DevTools → Console and run:

```js
sessionStorage.getItem('access_token')
```

Paste the token at **[jwt.io](https://jwt.io)** to inspect the claims and verify the RS256 signature.

---

## Run tests

```
./gradlew :apps:oauth2-demo:test
```

The integration tests cover: JWKS endpoint reachability, OIDC discovery, unauthenticated rejection of `/api/me`, and authenticated access using a mock JWT.

---

## Demo-only choices

These simplifications make the demo easy to run but are not production patterns:

- **In-memory RSA key pair** — regenerated on every restart; tokens issued before a restart become invalid
- **`{noop}` password encoding** — plaintext passwords; never do this in production
- **No HTTPS** — `crypto.subtle` works at `localhost` without TLS (secure context exception)
- **`requireAuthorizationConsent(false)`** — skips the consent screen; set to `true` to see it
