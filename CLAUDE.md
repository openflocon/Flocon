# Flocon

## Models

- **Planning a task: use Opus.** Analysis, design, architecture decisions, breaking a task into steps, and reviewing a plan are done with Opus.
- **Doing a task: use Sonnet.** Once a plan exists, implementing it (writing code, editing files, running checks, fixing CI, updating docs) is done with Sonnet.

When delegating to subagents, apply the same split: planning/exploration-for-design agents run on Opus, implementation agents run on Sonnet.

## Rules

Project rules live in `.claude/rules/` and are loaded automatically (path-scoped ones only when working in that folder):

- `android-sdk.md`: KMP SDK conventions, plugin / no-op parity (`FloconAndroid/`)
- `desktop-app.md`: desktop layering, DI and Room rules (`FloconDesktop/`)
- `compose.md`: Compose UI conventions: screen structure, state, design system, previews (`FloconDesktop/`)
- `git-workflow.md`: commit message format, branches, PRs
- `build-verification.md`: Gradle / MkDocs checks to run before a task is done
- `docs-protocol-sync.md`: keep `protocol/`, `docs/`, MCP docs and README in sync with code
