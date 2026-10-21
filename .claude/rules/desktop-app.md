---
paths:
  - "FloconDesktop/**"
---

# Desktop app rules (`FloconDesktop/`)

## Layering
Features are split across modules, one package per feature (e.g. `featureflags`) in each layer:

| Module | Package | Contents |
|---|---|---|
| `domain` | `io.github.openflocon.domain.<feature>` | `models/`, `repository/` (interfaces), `usecase/`, `DI.kt` |
| `data:core` | `io.github.openflocon.data.core.<feature>` | `datasource/` interfaces, `repository/` impls, `DI.kt` |
| `data:local` | `io.github.openflocon.data.local.<feature>` | Room `dao/`, `models/` entities, local datasources, `DI.kt` |
| `data:remote` | `com.flocon.data.remote.<feature>` | Websocket datasources, `model/` (wire models), `DI.kt` |
| `composeApp` | `io.github.openflocon.flocondesktop.features.<feature>` | ViewModels, `Navigation.kt`, `mapper/`, `model/` (UI models), `view/`, `DI.kt` |
| `mcp` | `io.github.openflocon.mcp.<feature>` | MCP tool provider, inputs, models, tests |

- Dependencies point inward: `composeApp` → `domain` ← `data:*`. `domain` never imports from `data` or `composeApp`.
- Each layer has its own model type (`*DomainModel`, `*Entity`, `*DataModel`, `*UiModel`) with explicit mappers. Don't leak entities or wire models into the UI.
- Use cases are single-purpose classes exposing `operator fun invoke(...)`, named `<Verb><Subject>UseCase` (e.g. `ObserveCurrentDeviceFeatureFlagsUseCase`).
- Register every new class in the layer's `DI.kt` (Koin) and include that module from the parent `DI.kt`.

## UI
See `compose.md`.

## Room database
- Adding/altering an entity requires bumping `version` in `AppDatabase` and committing the generated schema JSON under `composeApp/schemas/`. Never edit or delete an existing schema file.
