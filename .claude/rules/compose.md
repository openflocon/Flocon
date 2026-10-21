---
paths:
  - "FloconDesktop/**/*.kt"
---

# Compose conventions (`FloconDesktop/`)

Reference implementation: `composeApp/.../features/featureflags/`.

## Screen structure
- Each screen has two overloads with the same name:
  - A **stateful entry point** taking only `modifier`: gets the ViewModel with `koinViewModel()`, collects flows with `collectAsStateWithLifecycle()`, and delegates.
  - A **stateless overload** taking plain state + lambdas. This is the one that holds the UI, and the one previews call.
- Only the stateful entry point knows about the ViewModel. Never pass a ViewModel, use case or repository into a child composable.
- Navigation entries go in the feature's `Navigation.kt` (`sealed interface <Feature>Routes : FloconRoute`, `@Serializable` routes, `EntryProviderScope<FloconRoute>.<feature>Routes()`). Side panels implement `PanelRoute`.

## Composable signatures
- Parameter order: required state, then event lambdas, then `modifier: Modifier = Modifier`, then optional params, then trailing `content` slot.
- Every public composable that emits UI takes `modifier` and applies it **once, to its root**. Children start from a fresh `Modifier`.
- Events go up as lambdas named `onXxx` (`onProviderSelected`, `onFilterTextChanged`). When a screen has many row-level events, group them in a `sealed interface <Feature>Action` and pass a single `onAction: (<Feature>Action) -> Unit`.
- Pass method references (`viewModel::onAction`) instead of new lambdas where possible.
- Naming: full screens `<Feature>Screen`, reusable pieces `<Thing>View` (`FeatureFlagRowView`). Composables that return `Unit` are PascalCase nouns; ones that return a value are camelCase (`rememberFloconScrollbarAdapter`).

## State & stability
- UI models live in `model/`, are `data class`/`sealed interface` named `*UiModel`, and are annotated `@Immutable`.
- Collections passed to composables are `kotlinx.collections.immutable.ImmutableList` / `persistentListOf()`, never `List`.
- ViewModels expose `StateFlow<…UiModel>`, built with the `stateInWhileSubscribed(default)` helper (`common/utils/ViewModelExt.kt`, i.e. `SharingStarted.WhileSubscribed(5_000)`). Text field input that must stay responsive while typing may be exposed as a Compose `State<String>`.
- No business logic, mapping or formatting inside composables: do it in the ViewModel or `mapper/`.
- `remember` derived values that are costly; use `derivedStateOf` only when an input changes more often than the output.
- Lazy lists always give a stable `key` (`key = { _, item -> item.id }`).

## Design system
- Use `FloconTheme.colors` (bg, ink, accent, category and status tokens), `FloconTheme.typography` and `FloconTheme.shapes`. Do not use `MaterialTheme` or hardcoded `Color(...)` values in feature code.
- Reach for `library/designsystem/components` first (`FloconFeature`, `FloconPageTopBar`, `FloconButton`, `FloconTextField`, `FloconDialog`, `FloconVerticalScrollbar`, …). If a component is needed in more than one feature, add it there with a `Flocon` prefix rather than duplicating it.
- Padding, gaps and spacers only use `FloconTheme.spacing` (no `N.dp` literals; they would ignore the app scale). Sizes (icons, boxes, widths, radii, borders) are not spacing and can stay `dp` values.
- Fixed sizes used more than once in a file go in `private val` constants at file top (`private val NameWidth = 260.dp`).

## Resources
- All user-visible text uses `stringResource(Res.string.xxx)` backed by `composeResources/values/strings.xml`. No hardcoded strings, including content descriptions.

## Previews
- Every screen and non-trivial view has a `@Preview`. In new code it is `private`, named `<Composable>Preview`, and wraps content in `FloconTheme { }`.
- Preview data comes from `preview<UiModel>()` factory functions placed next to the UI model, not inline literals repeated across previews.
