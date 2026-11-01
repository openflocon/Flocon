# Flocon

## Models

- **Planning a task: use Opus.** Analysis, design, architecture decisions, breaking a task into steps, and reviewing a plan are done with Opus.
- **Doing a task: use Sonnet.** Once a plan exists, implementing it (writing code, editing files, running checks, fixing CI, updating docs) is done with Sonnet.

When delegating to subagents, apply the same split: planning/exploration-for-design agents run on Opus, implementation agents run on Sonnet.

## Workflow

Route each task by its nature:

| Task | Who | Model |
|---|---|---|
| Simple lookup or read (where is X, who uses Y, read a file) | `Explore` | haiku |
| Needs thinking (design, unclear bug, trade-off, big change) | `planner`, then `architect` for plugins / cross-module / protocol | opus |
| Everything else (write code, edit, tests, docs, CI fixes) | main session or `implementer` | sonnet |

Typical flow: `Explore` locates → `planner` decides (only if non-trivial) → `implementer` / main session codes → `test-runner` verifies → `reviewer` checks the diff.
The main session uses `opusplan` (`.claude/settings.json`): Opus in plan mode, Sonnet otherwise. The `dev-workflow` skill and the agent descriptions route tasks automatically, no need to name them.

## Who does what

Subagents live in `.claude/agents/`. The main session plans and writes code; delegate these jobs:

- **planner** (opus): designing the approach when the solution is not obvious. It returns steps a Sonnet implementer can follow.
- **implementer** (sonnet): carrying out a decided plan or a clear small change.
- **Explore** (haiku): finding where something is (files, symbols, usages). Use it instead of reading many files yourself. It returns paths, line numbers and one sentence each.
- **architect** (opus): before implementing a new plugin, a cross-module change or a protocol change, send it the plan. Apply its changes before starting.
- **test-runner** (haiku): running the module checks from `build-verification.md` after a change. It returns PASS/FAIL and only the failures.
- **reviewer** (sonnet): reviewing the diff before committing or opening a PR. Fix what it reports, or say why it isn't a problem.

Explore only locates code. Design exploration still follows the Opus rule above.

## Rules

Project rules live in `.claude/rules/` and are loaded automatically (path-scoped ones only when working in that folder):

- `android-sdk.md`: KMP SDK conventions, plugin / no-op parity (`FloconAndroid/`)
- `desktop-app.md`: desktop layering, DI and Room rules (`FloconDesktop/`)
- `compose.md`: Compose UI conventions: screen structure, state, design system, previews (`FloconDesktop/`)
- `git-workflow.md`: commit message format, branches, PRs
- `build-verification.md`: which Gradle / MkDocs checks to run locally, and which are left to CI
- `docs-protocol-sync.md`: keep `protocol/`, `docs/`, MCP docs and README in sync with code
