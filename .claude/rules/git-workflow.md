# Git & PR workflow

## Commits
- Conventional prefix, then an optional `[AREA]` tag in caps, then an imperative summary:
  - `feat: [MCP] list_images tool`
  - `fix: [NETWORK] Clear the list instantly when pressing the trash button`
  - `refact: [Screenshot] screenshot & media record`
  - `perf: [MCP] list_images filters in the Room query`
  - `feat(sample): test every feature from the sample app, per platform`
- Allowed prefixes: `feat`, `fix`, `refact`, `perf`, `docs`, `test`, `chore`.
- One logical change per commit. Don't mix formatting-only changes with behavior changes.
- Never commit secrets, `local.properties`, signing keys or build outputs.

## Branches & PRs
- Branch name is `<type>/<feature-kebab>` (e.g. `feat/device-selector-logos`), never `claude/...`. The `git-setup` skill applies this and the commit format; `create-pull-request` opens the PR.
- Never push directly to `main`; work on a feature branch and open a PR against `main`.
- Never force-push, rebase or amend on a branch someone else owns. Use merge commits to bring `main` in.
- PR title follows the commit format above (PRs are squash-merged, so the title becomes the commit).
- Use `.github/PULL_REQUEST_TEMPLATE/pull_request_template.md`: fill in `### Description` (what changed and why, which parts: SDK / Desktop / MCP / docs) and keep the `/gemini review` line.
- Open PRs as drafts by default (`gh pr create --draft`); mark them ready for review only when the user asks.
- Only open a PR when asked to.
