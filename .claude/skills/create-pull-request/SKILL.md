---
name: create-pull-request
description: Use only when the user asks to open a pull request on Flocon. Pushes the branch and creates the PR against main with a conventional title and the repo template. Runs in the Haiku `git-ops` agent: pass the checks already run and their results as arguments.
context: fork
agent: git-ops
---

# Create a pull request

Only run when the user explicitly asks for a PR.

Task from the caller: $ARGUMENTS

1. **Check the branch**: not `main`, working tree clean, name follows `git-setup` (`<type>/<feature-kebab>`). Otherwise fix it first following `.claude/skills/git-setup/SKILL.md`.
2. **Local checks**: the caller lists the checks from `.claude/rules/build-verification.md` it ran and their result. If it gives none, run them for the touched area. If the SDK public API or a no-op module changed, also run `cd FloconAndroid && ./gradlew :sample:android:assembleDebug :sample:android:assembleRelease`. If a check fails, stop and report it. State clearly any check that could not be run.
3. **Check sync**: protocol, `docs/`, MCP docs and README updated per `.claude/rules/docs-protocol-sync.md`.
4. **Push**: `git push -u origin <branch>`. Never force-push.
5. **Title**: same format as a commit, `<type>: [AREA] imperative summary` (PRs are squash-merged, the title becomes the commit).
6. **Body**: start from `.github/PULL_REQUEST_TEMPLATE/pull_request_template.md`.
   - Under `### Description`: what changed and why, and which parts are touched (SDK / Desktop / MCP / docs).
   - Keep the final `/gemini review` line.
   - No Claude attribution line.
7. **Create** (always as a draft, unless the user explicitly asks for a ready-for-review PR): write the body to a temp file, then `gh pr create --draft --base main --title "<title>" --body-file <file>`.
8. **Report** the PR URL and the checks run. The calling session then calls the ccd_pr `get_status` tool, `bind_pr` if it doesn't report the PR, and offers Auto-fix. Don't poll CI and don't enable auto-merge unless asked.
