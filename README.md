## Repository structure & conventions

This is a root for demo projects which should demonstrate use of technologies
Some ideas:
- [ ] WebSockets
- [x] Server-Sent Events

This repository is organized as a **Gradle multi-project monorepo**.

### Structure

```
dev-demos/
├─ apps/                     # independent demo applications
│  ├─ server-sent-events-demo/
│  ├─ websockets-demo/
│  └─ ... (future demos)
├─ libs/                     # shared libraries or utilities (optional)
├─ build.gradle.kts          # common Gradle configuration
├─ settings.gradle.kts       # includes all subprojects
├─ gradle/                   # Gradle wrapper
└─ README.md
```

### Conventions

- Each demo app lives under `apps/` and has its own `build.gradle.kts`.
- Shared code may go into `libs/` (e.g., `libs/common-web`).
- Only the **root** project contains:
    - `settings.gradle.kts`
    - Gradle wrapper (`gradlew`, `gradle/`)
    - `.idea/` (IDE configuration)

### Gradle usage

```
# Run a specific demo
./gradlew :apps:server-sent-events-demo:bootRun

# Build all demos
./gradlew build

# List all projects
./gradlew projects
```

### IntelliJ IDEA

- Open the **root folder** (`dev-demos/`) — IntelliJ will import all submodules automatically.
- Each app can also be opened individually if needed (as a standalone Gradle project).

### Git workflow

- Commit messages can be prefixed by module for clarity, e.g.
    - `sse: add disconnect button`
    - `ws: initial websocket demo`
- Use pathspecs when committing changes from a specific app:

```
git add apps/server-sent-events-demo
git commit -m "sse: fix SSE reconnection logic"
```

---

This structure keeps all demo applications in one place while allowing each to build and run independently.
