---
name: dev-workflow
description: Use for ANY development request on Flocon (new feature, bug fix, refactor, docs or test change), even when the user names no skill. Routes the work to the right model and subagents.
---

# Dev workflow

Classify the request, then follow the matching path. Do not ask the user which one.

## 1. Setup
Load `git-setup` before creating a branch or a commit.

## 2. Route by nature

| Request | Path |
|---|---|
| Lookup, "where is X", reading code | `Explore` (haiku), nothing else |
| Needs thinking: design, unclear bug, trade-off, more than 2 files | Plan mode (Opus), `planner` for the approach |
| Adds a plugin, spans modules or changes the protocol | `planner`, then `architect` on the plan before coding |
| Clear change, plan already decided | Code directly (Sonnet), or `implementer` |

## 3. Verify and deliver
1. `test-runner` on the touched module (`.claude/rules/build-verification.md`).
2. `reviewer` on the diff. Fix what it reports, or say why it isn't a problem.
3. Docs / protocol sync per `.claude/rules/docs-protocol-sync.md`.
4. Open a PR only when asked, with `create-pull-request`.
