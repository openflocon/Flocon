# Docs & protocol sync

Code, protocol and docs ship together in the same PR. When a change touches one of these, update the others.

## Protocol (mobile ⇄ desktop messages)
- The source of truth is duplicated in two files that must stay identical in plugin/method names:
  - `FloconAndroid/flocon/src/commonMain/kotlin/io/github/openflocon/flocon/Protocol.kt`
  - `FloconDesktop/domain/src/commonMain/kotlin/io/github/openflocon/domain/Protocol.kt`
- Any new/changed plugin, method or payload field → update `protocol/<plugin>.md` (and the plugin table in `protocol/README.md` for a new plugin).
- Keep changes backward compatible: new fields optional with defaults; don't rename or remove fields an older SDK/desktop still sends.

## User docs (`docs/`, published with MkDocs)
- New feature → new `docs/<feature>.md` and an entry in the `nav:` of `mkdocs.yml`.
- Changed setup / public SDK API → update `docs/setup.md`, the feature page, and the snippet in the root `README.md`. Breaking changes also go in `docs/migration.md`.

## MCP
- New or changed MCP tool → update `docs/mcp.md` and `plugins/flocon/skills/flocon-debug/SKILL.md`.

## Full-feature checklist
A new plugin (see the feature flags PR as the reference) touches: SDK module + no-op, both `Protocol.kt`, desktop domain/data/UI, MCP tools + tests, sample apps, `protocol/`, `docs/`, `mkdocs.yml`, `README.md`, and the MCP skill.
