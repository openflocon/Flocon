# Build & verification

CI runs the full build of the touched area on every PR. Locally, check only what the change can break: the touched module while iterating, and the few checks CI doesn't run before opening a PR. Run from the module root (JDK 21).

## While working on a task

Run the tests of the module(s) you changed, not the full build:

| Touched | Command (examples) |
|---|---|
| An SDK plugin in `FloconAndroid/` | `./gradlew :featureflags:jvmTest` (add `:<plugin>-no-op:compileKotlinJvm` when the no-op changes) |
| A desktop module in `FloconDesktop/` | `./gradlew :mcp:allTests`, `./gradlew :data:core:allTests` |
| `docs/**` or `mkdocs.yml` | `mkdocs build --strict` |

## Before opening a PR

Only run the checks CI doesn't cover:

| Touched | Command | Why |
|---|---|---|
| `FloconAndroid/**` public API or no-op modules | `cd FloconAndroid && ./gradlew :sample:android:assembleDebug :sample:android:assembleRelease` | debug = real impl, release = no-op; not run by `android_pr_check.yml` |

## Left to CI

The full builds run on every PR. Don't run them locally unless CI fails and you need to reproduce:

| Touched | CI command | Workflow |
|---|---|---|
| `FloconAndroid/**` | `./gradlew jvmTest test assemble` | `android_pr_check.yml` |
| `FloconDesktop/**` | `./gradlew allTests assemble` | `desktop_pr_check.yml` |
| `docs/**` or `mkdocs.yml` | `mkdocs build --strict` | `docs.yml` |

A task is done when its module checks pass locally. The PR is done when CI is green; if CI fails, reproduce with the CI command and fix the cause.

- New behavior gets a test: SDK plugins in `src/jvmTest`, MCP tools in `mcp/src/desktopTest`.
- Never skip, disable or `@Ignore` a test to get green. Fix the cause.
- If a check can't be run in the current environment, say so explicitly in the final report rather than claiming it passed.
