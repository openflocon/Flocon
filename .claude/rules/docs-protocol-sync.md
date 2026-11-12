# Docs & protocol sync

Code, protocol and docs ship together in the same PR. When a change touches one of these, update the others.

## Protocol (mobile ⇄ desktop messages)
- The source of truth is duplicated in two files that must stay identical in plugin/method names:
  - `FloconAndroid/flocon/src/commonMain/kotlin/io/github/openflocon/flocon/Protocol.kt`
  - `FloconDesktop/domain/src/commonMain/kotlin/io/github/openflocon/domain/Protocol.kt`
- Any new/changed plugin, method or payload field → update `internal-docs/protocol/<plugin>.md` (and the plugin table in `internal-docs/protocol/README.md` for a new plugin).
- Keep changes backward compatible: new fields optional with defaults; don't rename or remove fields an older SDK/desktop still sends.

## User docs (`docs/`)
The user docs live in `docs/` and are published with MkDocs (`mkdocs.yml`). Each feature page has Kotlin / Swift / Flutter tabs: when a change affects one SDK, update its tab only.
- New feature → new `docs/<feature>.md`.
- Versions in snippets are always the `LAST_VERSION` placeholder (`:LAST_VERSION`, `from: "LAST_VERSION"`, `^LAST_VERSION`), never a number: the badges on `docs/setup.md` and the READMEs show the current one.
- Changed setup / public SDK API → update `docs/setup.md`, the feature page, and the README. Breaking changes also go in `docs/migration.md`.

## MCP
- New or changed MCP tool → update `docs/mcp.md` and `plugins/flocon/skills/flocon-mcp/SKILL.md`.

## Full-feature checklist
A new plugin (see the feature flags PR as the reference) touches: SDK module + no-op, both `Protocol.kt`, desktop domain/data/UI, MCP tools + tests, sample apps, `internal-docs/protocol/`, `docs/`, `README.md`, and the MCP skill.
