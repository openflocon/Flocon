---
name: dev-workflow
description: Use for ANY development request on Flocon (new feature, bug fix, refactor, docs or test change), even when the user names no skill. Routes the work to the right model and subagents.
---

# Dev workflow

Classify the request, then follow the matching path. Do not ask the user which one.

## 1. Setup
Run the `git-setup` skill (it runs in the Haiku `git-ops` agent) to name the branch, passing the feature name.

## 2. Route by nature

| Request | Path |
|---|---|
| Lookup, "where is X", reading code | `Explore` (haiku), nothing else |
| CI failed on a PR | `ci-investigator` (haiku) for the error, then fix the cause (Sonnet) |
| Docs-only change | `docs-sync` (haiku) or code directly |
| Needs thinking: design, unclear bug, trade-off | Plan mode (Opus), `planner` for the approach |
| Adds a plugin, changes the protocol or the layering between modules | `planner`, then `architect` on the plan before coding |
| Clear change, plan already decided (even across several files) | Code directly (Sonnet), or `implementer` |

## 3. Verify and deliver
1. `test-runner` (haiku) on the touched module (`.claude/rules/build-verification.md`).
2. `docs-sync` (haiku) when the change touches the protocol, an MCP tool, the public SDK API or adds a feature (`.claude/rules/docs-protocol-sync.md`).
3. `reviewer` (sonnet) once on the finished diff. Fix what it reports, or say why it isn't a problem.
4. Commit with `git-setup`, and open a PR only when asked, with `create-pull-request`, passing the checks run and their results. Both run in the Haiku `git-ops` agent. Then bind the PR with the ccd_pr tools.
