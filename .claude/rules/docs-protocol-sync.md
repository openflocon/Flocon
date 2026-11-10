# Docs & protocol sync

Code, protocol and docs ship together in the same PR. When a change touches one of these, update the others.

## Protocol (mobile ⇄ desktop messages)
- The source of truth is duplicated in two files that must stay identical in plugin/method names:
  - `FloconAndroid/flocon/src/commonMain/kotlin/io/github/openflocon/flocon/Protocol.kt`
  - `FloconDesktop/domain/src/commonMain/kotlin/io/github/openflocon/domain/Protocol.kt`
- Any new/changed plugin, method or payload field → update `internal-docs/protocol/<plugin>.md` (and the plugin table in `internal-docs/protocol/README.md` for a new plugin).
- Keep changes backward compatible: new fields optional with defaults; don't rename or remove fields an older SDK/desktop still sends.

## User docs (`docs2/`)
The docs now live in `docs2/`. Do not touch `docs/` (the previous docs): a change to the user docs goes in `docs2/` only.
- New feature → new `docs2/<feature>.md`.
- Changed setup / public SDK API → update `docs2/setup.md`, the feature page, and the snippet in the root `README.md`. Breaking changes also go in `docs2/migration.md`.

## MCP
- New or changed MCP tool → update `docs2/mcp.md` and `plugins/flocon/skills/flocon-mcp/SKILL.md`.

## Full-feature checklist
A new plugin (see the feature flags PR as the reference) touches: SDK module + no-op, both `Protocol.kt`, desktop domain/data/UI, MCP tools + tests, sample apps, `internal-docs/protocol/`, `docs2/`, `README.md`, and the MCP skill.
