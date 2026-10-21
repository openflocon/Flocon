---
paths:
  - "**/*.kt"
  - "**/*.kts"
---

# Kotlin style (non-Compose code)

Applies to all Kotlin in `FloconAndroid/` and `FloconDesktop/`. Compose UI has extra rules in `compose.md`.

## Formatting
- Official Kotlin style (`kotlin.code.style=official`) and `FloconDesktop/.editorconfig` (ktlint `intellij_idea`): 4-space indent, no tabs, final newline.
- No wildcard imports. Remove unused imports. Keep imports sorted alphabetically.
- Use trailing commas in multi-line parameter and argument lists.
- When a declaration or call doesn't fit on one line, put one parameter / argument per line.
- Don't reformat code you aren't otherwise changing.

## Naming
- Classes and objects: PascalCase. Functions and properties: camelCase. `const val`: both `SCREAMING_SNAKE_CASE` and PascalCase exist; match the surrounding file (`Protocol` uses PascalCase: `Protocol.FromDevice.FeatureFlags.Plugin`).
- Suffix by role: `*UseCase`, `*Repository` / `*RepositoryImpl`, `*DataSource` / `*DataSourceRoom` / `*DataSourceImpl`, `*Dao`, `*Entity`, `*DomainModel`, `*DataModel`, `*UiModel`, `*ViewModel`. SDK public types are prefixed `Flocon` (`FloconFeatureFlagsPlugin`).
- Wire models in the SDK are `internal @Serializable` classes suffixed `*Serializable`.
- Booleans read as a question: `isJson`, `hasPayload`, `isNewDevice`.

## Functions & calls
- Use named arguments for any call with more than one argument, and always for arguments of the same type next to each other (`send(provider = provider, flags = flags)`).
- Prefer expression bodies (`= ...`) for single-expression functions. Use a block body once there's a local `val` or more than one statement.
- Put mappers in a `mapper/` package as extension functions: `toXxx()` / `mapToUi()` / `toDomain()`. They're `internal` or `private` unless another module needs them.
- Exhaustive `when` over sealed types and enums; no `else ->` branch that hides a newly added case.

## Visibility & types
- Default to the narrowest visibility: `private` in the file, `internal` in the module. In the SDK, only the public API lives in non-`internal` declarations.
- Declare explicit return types on public functions and properties.
- Prefer `val` and read-only types (`List`, `Map`). Keep `Mutable*` collections private.
- Use `data class` for value holders, `sealed interface` for closed hierarchies, `object` for stateless singletons.

## Null safety & errors
- No `!!` in new code: use `?.let`, `?:`, `requireNotNull` / `checkNotNull` with a message, or an early `return`.
- Don't swallow exceptions silently. In the SDK, catch around network/encoding work and log with `FloconLogger.logError(...)`, so the host app never crashes because of Flocon.
- Never catch `CancellationException` without rethrowing it.

## Coroutines
- Expose `suspend` functions and `Flow` instead of callbacks.
- Desktop: never hardcode `Dispatchers.*`. Inject `DispatcherProvider` and use `withContext(dispatcherProvider.data)` / `.flowOn(dispatcherProvider.data)` in repositories.
- SDK: a plugin owns its own `CoroutineScope(dispatcher + SupervisorJob())`, with the dispatcher injected as a constructor parameter that has a default (for tests).
- No `GlobalScope`, and no `runBlocking` outside tests and `main()`.

## Dependency injection
- Constructor injection only; dependencies are `private val` constructor parameters.
- Desktop: register in the layer's Koin `DI.kt` with `factoryOf(::X)` / `singleOf(::X)`; the module `val` is `internal`.

## Comments
- Comment the *why* (threading assumptions, protocol quirks, workarounds), not the *what*. Lowercase, short, `//` style, as in the existing code.
- KDoc (`/** */`) on every public SDK API, since it's what library users read.
- No commented-out code, and no `TODO` without context.
