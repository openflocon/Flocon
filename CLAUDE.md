# Flocon

## Models

- **Planning a task: use Opus.** Analysis, design, architecture decisions, breaking a task into steps, and reviewing a plan are done with Opus.
- **Doing a task: use Sonnet.** Once a plan exists, implementing it (writing code, editing files, running checks, fixing CI, updating docs) is done with Sonnet.

- **Mechanical work: use Haiku.** Lookups, running checks, reading CI logs, syncing docs from a finished diff, and git / `gh` operations go to the Haiku agents below. Writing or reviewing Kotlin stays on Sonnet.

When delegating to subagents, apply the same split: planning/exploration-for-design agents run on Opus, implementation agents run on Sonnet, mechanical agents run on Haiku.

## Workflow

Route each task by its nature:

| Task | Who | Model |
|---|---|---|
| Simple lookup or read (where is X, who uses Y, read a file) | `Explore` | haiku |
| Run module checks | `test-runner` | haiku |
| CI failed: find out why | `ci-investigator` | haiku |
| Update `internal-docs/protocol/`, `docs/`, README, MCP skill after a code change | `docs-sync` | haiku |
| Branch, commit, PR | `git-setup` / `create-pull-request` skills (run in `git-ops`) | haiku |
| Needs thinking (design, unclear bug, trade-off) | `planner`, then `architect` for new plugins / protocol / layering changes | opus |
| Everything else (write Kotlin, tests, fix the cause of a CI failure, review) | main session, `implementer` or `reviewer` | sonnet |
| See Flocon Desktop with data and no phone (doc screenshots, UI states, MCP tests, edge cases) | `flocon-mock-mode` skill (`--mock=<scenario>`) | any |

Typical flow: `Explore` locates → `planner` decides (only if the approach isn't obvious) → `implementer` / main session codes → `test-runner` verifies → `docs-sync` updates the docs → `reviewer` checks the finished diff → `create-pull-request` opens the PR.
The main session uses `opusplan` (`.claude/settings.json`): Opus in plan mode, Sonnet otherwise. The `dev-workflow` skill and the agent descriptions route tasks automatically, no need to name them.

## Who does what

Subagents live in `.claude/agents/`. The main session plans and writes code; delegate these jobs:

- **planner** (opus): designing the approach when the solution is not obvious. It returns steps a Sonnet implementer can follow.
- **implementer** (sonnet): carrying out a decided plan or a clear small change.
- **Explore** (haiku): finding where something is (files, symbols, usages). Use it instead of reading many files yourself. It returns paths, line numbers and one sentence each.
- **architect** (opus): before implementing a new plugin, a protocol change or a change to the layering between modules, send it the plan. Apply its changes before starting.
- **test-runner** (haiku): running the module checks from `build-verification.md` after a change. It returns PASS/FAIL and only the failures.
- **ci-investigator** (haiku): when a PR check fails, it reads the CI logs and returns the failing task and error lines. Don't read CI logs yourself.
- **docs-sync** (haiku): once the code is done and tested, it updates `internal-docs/protocol/`, `docs/`, `mkdocs.yml`, README and the MCP skill from the diff, then runs `mkdocs build --strict`.
- **git-ops** (haiku): runs the `git-setup` and `create-pull-request` skills (branch, commits, draft PR). The main session then binds the PR with the ccd_pr tools.
- **reviewer** (sonnet): reviewing the finished diff once, before committing or opening a PR, not after every edit. Fix what it reports, or say why it isn't a problem.

Explore only locates code. Design exploration still follows the Opus rule above.

## Rules

Project rules live in `.claude/rules/` and are loaded automatically (path-scoped ones only when working in that folder):

- `android-sdk.md`: KMP SDK conventions, plugin / no-op parity (`FloconAndroid/`)
- `desktop-app.md`: desktop layering, DI and Room rules (`FloconDesktop/`)
- `compose.md`: Compose UI conventions: screen structure, state, design system, previews (`FloconDesktop/`)
- `git-workflow.md`: commit message format, branches, PRs
- `build-verification.md`: which Gradle / MkDocs checks to run locally, and which are left to CI
- `docs-protocol-sync.md`: keep `internal-docs/protocol/`, `docs/`, MCP docs and README in sync with code
