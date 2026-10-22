#!/usr/bin/env bash
# PreToolUse hook (Bash): before a `git commit`, format the staged FloconDesktop Kotlin files with ktlint
# and re-stage them. Never blocks the commit.
command -v jq >/dev/null && command -v ktlint >/dev/null || exit 0

cmd=$(jq -r '.tool_input.command // empty')
[[ "$cmd" == *"git commit"* ]] || exit 0

root=$(git rev-parse --show-toplevel 2>/dev/null) || exit 0
cd "$root" || exit 0

files=$(git diff --cached --name-only --diff-filter=ACM -- 'FloconDesktop/*.kt' 'FloconDesktop/*.kts')
[ -n "$files" ] || exit 0

cd FloconDesktop || exit 0
echo "$files" | sed 's#^FloconDesktop/##' | xargs ktlint -F >&2 || true
cd "$root" && echo "$files" | xargs git add
exit 0
