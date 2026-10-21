# Build & verification

A task is not done until the checks CI runs for the touched area pass locally. Run from the module root (JDK 21).

| Touched | Command | Mirrors |
|---|---|---|
| `FloconAndroid/**` | `cd FloconAndroid && ./gradlew jvmTest test assemble` | `android_pr_check.yml` |
| `FloconAndroid/**` public API or no-op modules | `cd FloconAndroid && ./gradlew :sample:android:assembleDebug :sample:android:assembleRelease` | release check (debug = real impl, release = no-op) |
| `FloconDesktop/**` | `cd FloconDesktop && ./gradlew allTests assemble` | `desktop_pr_check.yml` |
| `docs/**` or `mkdocs.yml` | `mkdocs build --strict` | `docs.yml` |

- While iterating, narrow to the changed module (e.g. `./gradlew :featureflags:jvmTest`, `./gradlew :mcp:allTests`), then run the full command before finishing.
- New behavior gets a test: SDK plugins in `src/jvmTest`, MCP tools in `mcp/src/desktopTest`.
- Never skip, disable or `@Ignore` a test to get green. Fix the cause.
- If a check can't be run in the current environment, say so explicitly in the final report rather than claiming it passed.
