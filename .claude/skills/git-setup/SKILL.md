---
name: git-setup
description: Use before starting work on a Flocon feature, and before creating a branch or a commit. Sets the branch name (the feature name, never `claude/`) and the commit message format.
---

# Git setup: branch & commit naming

## Branch

Format: `<type>/<feature-kebab>`

- `type` is one of `feat`, `fix`, `refact`, `perf`, `docs`, `test`, `chore` (same as the commit prefix).
- The rest is the feature name: lowercase kebab-case, 2 to 5 words.
- Never `claude/`, `worktree-` or random suffixes.

Examples: `feat/device-selector-logos`, `fix/network-clear-instant`, `chore/ktlint-multiline-if-else`, `feat/mcp-take-flocon-screenshot`.

Steps:
1. Never commit on `main`.
2. If the current branch starts with `claude/` or `worktree-` and has not been pushed, rename it: `git branch -m <type>/<feature-kebab>`.
3. If it was already pushed or belongs to someone else, don't rename it; tell the user.

## Commit message

Format: `<type>: [AREA] imperative summary`

- Same `type` list as the branch.
- `[AREA]` in caps, reusing the areas already in `git log` (`MCP`, `NETWORK`, `DESIGN`, `TIMELINE`, `Desktop`, `OKHTTP`...). Use `feat(sample): ...` for sample apps.
- English, imperative, under 72 characters, no trailing period.
- One logical change per commit; never mix formatting-only changes with behavior changes.
- Never commit secrets, `local.properties`, signing keys or build outputs.
- No Claude attribution lines (no `Co-Authored-By`, no "Generated with").

Good: `feat: [MCP] list_images tool`, `fix: [NETWORK] Clear the list instantly when pressing the trash button`.
Bad: `Update stuff`, `feat: added a new tool.`, `fix(network): Fixes`.
