# Flocon

## Models

- **Planning a task: use Opus.** Analysis, design, architecture decisions, breaking a task into steps, and reviewing a plan are done with Opus.
- **Doing a task: use Sonnet.** Once a plan exists, implementing it (writing code, editing files, running checks, fixing CI, updating docs) is done with Sonnet.

When delegating to subagents, apply the same split: planning/exploration-for-design agents run on Opus, implementation agents run on Sonnet.

## Who does what

Subagents live in `.claude/agents/`. The main session writes code; delegate these jobs without waiting to be asked:

- **Plan** (opus): before editing any file, when a task touches more than 2 files, adds a plugin, crosses modules or changes the protocol. It returns the steps; follow them.
- **Explore** (haiku): every time you need to find where something is (files, symbols, usages). Use it instead of chaining grep / glob / read calls yourself. It returns paths, line numbers and one sentence each.
- **architect** (opus): once the plan exists, for a new plugin, a cross-module change or a protocol change, and before writing code. Apply its changes before starting.
- **test-runner** (haiku): after every code or docs change, to run the module checks from `build-verification.md`. It returns PASS/FAIL and only the failures. Don't run Gradle or MkDocs yourself.
- **reviewer** (sonnet): before every commit and before opening a PR. Fix what it reports, or say why it isn't a problem.

Typical order: Explore → Plan → architect (if needed) → implement → test-runner → reviewer → commit.

Explore only locates code. Design exploration still follows the Opus rule above.

## Rules

Project rules live in `.claude/rules/` and are loaded automatically (path-scoped ones only when working in that folder):

- `android-sdk.md`: KMP SDK conventions, plugin / no-op parity (`FloconAndroid/`)
- `desktop-app.md`: desktop layering, DI and Room rules (`FloconDesktop/`)
- `compose.md`: Compose UI conventions: screen structure, state, design system, previews (`FloconDesktop/`)
- `git-workflow.md`: commit message format, branches, PRs
- `build-verification.md`: which Gradle / MkDocs checks to run locally, and which are left to CI
- `docs-protocol-sync.md`: keep `protocol/`, `docs/`, MCP docs and README in sync with code
