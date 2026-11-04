# MCP timeline: `show_in_timeline` (PR 3b)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** L'agent montre à l'utilisateur une zone de la timeline (un crash, des appels en boucle, un appel volumineux): Flocon affiche la timeline, sélectionne l'événement et centre le graphe, puis l'agent dit d'aller voir dans l'app.

**Architecture:** Même schéma que la PR 2 (demande en mémoire, consommée par l'UI), mais pour le focus: un `TimelineFocusRepository` (interface dans `domain`, implémentation en mémoire dans `data:core`) et des use cases. `TimelineViewModel` consomme la demande **quand la cible est devenue l'appareil courant**, car la demande d'écran de la PR 2 est consommée tout de suite par `AppViewModel` alors que le focus doit attendre l'écran Timeline. Le centrage du graphe (qui n'existe pas) est une fonction pure testée. Les filtres de l'utilisateur ne sont jamais modifiés: l'outil lit leur état et dit si l'événement est caché.

**Tech Stack:** Kotlin Multiplatform, Compose Desktop, Koin, MCP Kotlin SDK.

**Spec:** `docs/superpowers/specs/2026-11-03-mcp-timeline-design.md` (outil 2). Dépend de la PR 2 (`DesktopNavigationRepository`, `flocon_navigate_into_desktop_app`) et de la PR 3a (`GetTimelineEventsUseCase`, `TimelineEventRef`, `TimelineWindowResolver`).

## Global Constraints

- Outil nommé `show_in_timeline`; annotations `writeToolAnnotations(destructive = false, idempotent = true)`.
- Jamais de passage de Flocon au premier plan; la réponse demande à l'utilisateur d'aller voir dans l'app et dit que la demande est enregistrée (l'outil ne sait pas si la fenêtre est visible).
- Tout est validé **avant** la première écriture: un événement inconnu ou une fenêtre sans événement ne change ni la sélection, ni le mode, ni l'écran.
- Les filtres de l'utilisateur (recherche, sources cochées, « session courante seulement ») ne sont jamais modifiés.
- Le mode Graph est écrit dans les préférences de l'utilisateur (écrasement accepté): `SetTimelineGraphUseCase`.
- Demande de focus: remplacée par la suivante, expire après 10 minutes (même durée que la PR 2).
- `mcp` ne dépend pas de `composeApp`; le fournisseur d'état des filtres est une interface de `domain`.
- Le mode Liste ne fait que sélectionner la ligne (la pagination n'a pas de « défiler jusqu'à »): la réponse le dit.
- Commits `feat: [MCP] <résumé>`; branche `feat/mcp-show-in-timeline` depuis `main`. JDK 21.
- À la fin de cette PR: supprimer `docs/superpowers/` et l'entrée `exclude_docs: superpowers/` de `mkdocs.yml`.

## Review Focus

- Deux appareils: le focus n'est appliqué qu'une fois la cible devenue l'appareil courant; une demande expirée ou remplacée n'est jamais appliquée.
- Événement caché par un filtre de l'utilisateur (source décochée, recherche texte, facettes, règles réseau, « session courante seulement »): la réponse le dit, avec la raison, et rien n'est changé.
- Graphe: l'application du focus ne doit pas être écrasée par le viewport par défaut au premier affichage, ni déplacée par le défilement automatique du direct.
- Session qui n'est pas la dernière: le graphe bascule sur la bonne session, puis revient normalement.
- Aucun événement ou fenêtre vide: erreur claire, aucun effet de bord.

---

## File Structure

- `domain/.../timeline/models/TimelineFocusRequestDomainModel.kt`, `repository/TimelineFocusRepository.kt`, `repository/TimelineUserFilterProvider.kt`, `usecase/` (`RequestTimelineFocusUseCase`, `ObserveTimelineFocusUseCase`, `ConsumeTimelineFocusUseCase`, `GetTimelineEventHiddenReasonsUseCase`), `DI.kt`.
- `data/core/.../timeline/repository/TimelineFocusRepositoryImpl.kt` + `DI.kt`.
- `composeApp/.../features/timeline/`: `TimelineFilterStore.kt` (implémente `TimelineUserFilterProvider`), `TimelineViewModel.kt`, `graph/GraphLayout.kt` (`focusedViewport`), `graph/TimelineGraph.kt`, `DI.kt`.
- `mcp/.../timeline/ShowInTimelineMcpToolProvider.kt` (+ Inputs/Models), `mcp/.../screen/McpSelectionApplier.kt` (extrait de la PR 2), `mcp/DI.kt`, `FloconMcpServer.kt`.
- Docs: `docs/mcp.md`, skill `flocon-mcp`, nettoyage `docs/superpowers/` et `mkdocs.yml`.

### Task 1: la demande de focus (domain + data:core)

**Interfaces:**
- Produces: `TimelineFocusRequestDomainModel(target: DeviceIdAndPackageNameDomainModel, sessionStart: Long?, event: TimelineEventRef?, fromMs: Long, toMs: Long, requestedAtMs: Long)`; `TimelineFocusRepository { fun requestFocus(...); fun observeFocus(): Flow<TimelineFocusRequestDomainModel>; fun consumeFocus(request) }` (mêmes règles que `DesktopNavigationRepository`: remplacement, expiration 10 min, `compareAndSet` à la consommation); use cases `RequestTimelineFocusUseCase`, `ObserveTimelineFocusUseCase`, `ConsumeTimelineFocusUseCase`.

- [ ] **Step 1: tests** `data/core/.../TimelineFocusRepositoryImplTest.kt`: `a request becomes pending`, `a newer request replaces the pending one`, `consuming an older request keeps a newer one`, `an expired request is not emitted`, `a request exactly at the ttl is still emitted` (même forme que `DesktopNavigationRepositoryImplTest`, horloge injectée).
- [ ] **Step 2: Run** `./gradlew :data:core:desktopTest --tests '*TimelineFocusRepositoryImplTest*'`. Attendu: FAIL.
- [ ] **Step 3: Implement** l'interface, l'implémentation en mémoire (`single<TimelineFocusRepository> { TimelineFocusRepositoryImpl() }`) et les use cases enregistrés dans les `DI.kt`.
- [ ] **Step 4: Run** `./gradlew :data:core:desktopTest :domain:desktopTest`. Attendu: PASS.
- [ ] **Step 5: Commit** `feat: [MCP] Timeline focus request`

### Task 2: détecter un événement caché sans toucher aux filtres

**Interfaces:**
- Produces: `interface TimelineUserFilterProvider { suspend fun current(): TimelineFilterDomainModel }` (domain). `enum TimelineHiddenReason { Source, Text, Facets, NetworkRules, CurrentSessionOnly, Combination }`. `GetTimelineEventHiddenReasonsUseCase(userFilter: TimelineUserFilterProvider, getEvents: GetTimelineEventsUseCase)` avec `suspend operator fun invoke(target, ref: TimelineEventRef): List<TimelineHiddenReason>` (liste vide = visible).
- `TimelineFilterStore` implémente le fournisseur: `current()` assemble le filtre depuis les StateFlows et `storedSources.first()`, sans debounce (le flux `filter` est un `shareIn(WhileSubscribed)` et n'a pas de valeur si la timeline n'a jamais été ouverte). Liaison dans `composeApp/.../timeline/DI.kt`: `singleOf(::TimelineFilterStore) bind TimelineUserFilterProvider::class`.

- [ ] **Step 1: tests** `GetTimelineEventHiddenReasonsUseCaseTest` (faux fournisseur et faux dépôt): `a visible event gives no reason`, `an event hidden only by an unchecked source gives Source`, `hidden only by the text search gives Text`, `hidden only by current-session-only gives CurrentSessionOnly`, `hidden by two filters gives Combination`.
- [ ] **Step 2: Run**, FAIL; **Step 3: Implement**: relire l'événement (par référence) avec le filtre de l'utilisateur; s'il manque, réessayer en remettant chaque composant à zéro **un par un**; **Step 4: Run** `./gradlew :domain:desktopTest :composeApp:desktopTest`, PASS.
- [ ] **Step 5: Commit** `feat: [MCP] Know which user filters hide a timeline event`

### Task 3: l'UI applique le focus (`TimelineViewModel` + graphe)

**Interfaces:**
- Produces: `GraphFocusUiModel(fromMs: Long, toMs: Long, token: Long)` (relatif au début de session); `TimelineViewModel.graphFocus: StateFlow<GraphFocusUiModel?>`; `fun focusedViewport(fromMs: Long, toMs: Long, trackWidthPx: Float): GraphViewport` dans `graph/GraphLayout.kt` (la plage + 20 % de marge, au moins 2 s de large, `pxPerMs` borné à `[MIN_PX_PER_MS, MAX_PX_PER_MS]`, `startMs >= MIN_START_MS`); `TimelineGraph(focus: GraphFocusUiModel?, onFocusApplied: () -> Unit, ...)`.

- [ ] **Step 1: tests** `GraphLayoutTest`: `the focused range is centered with a margin`, `a range under 2 s is widened to 2 s`, `the zoom is clamped`, `the start never goes below the minimum`.
- [ ] **Step 2: tests ViewModel** (si un harnais existe; sinon extraire la décision en fonction pure testée): `the focus waits for its target to be the current device`, `an expired request is ignored`, `the focus selects the event with the key source:id`, `a focus on an older session sets that session`.
- [ ] **Step 3: Run**, FAIL; **Step 4: Implement.** `TimelineViewModel` reçoit `ObserveTimelineFocusUseCase` et `ConsumeTimelineFocusUseCase`, attend `currentDeviceIdAndPackageName == request.target`, règle la session du graphe (null si c'est la dernière), la sélection (`_selectedKey`, format de `callKey`), émet `graphFocus`, puis consomme. `TimelineGraph` applique le focus dans un `LaunchedEffect(focus, opened)` **après** l'ouverture (sinon l'effet du viewport par défaut l'écrase), en mettant `fitted = false` et `paused = true` pour que le direct ne déplace pas la vue.
- [ ] **Step 5: Run** `./gradlew :composeApp:desktopTest ktlintFormat`, puis un essai à la main (voir Task 5). **Step 6: Commit** `feat: [MCP] The timeline applies a focus request`

### Task 4: l'outil `show_in_timeline`

**Files:** `ShowInTimelineMcpToolProvider.kt`, `ShowInTimelineMcpInputs.kt`, `ShowInTimelineMcpModels.kt`, `McpSelectionApplier.kt`, `NavigateDesktopAppMcpToolProvider.kt` (réutilise l'applicateur), `mcp/DI.kt`; test `ShowInTimelineMcpToolProviderTest.kt`.

**Interfaces:**
- Consumes: `GetTimelineEventsUseCase`, `TimelineWindowResolver` (PR 3a), `GetTimelineEventHiddenReasonsUseCase`, `RequestTimelineFocusUseCase`, `RequestDesktopScreenUseCase`, `SetTimelineGraphUseCase`.
- Produces: `ShowInTimelineInput : McpTargetArguments` (`eventId?`, `at?`, `from?`, `to?`, `view` `graph|list` défaut `graph`, `deviceId`, `packageName`); sans repère, l'outil prend l'anomalie principale de la fenêtre par défaut. `ShowInTimelineMcpResult(applied: List<String>, focus: ..., hiddenBy: List<String>, message: String)`. `McpSelectionApplier` fait « sélectionner l'appareil, puis l'app » (extrait de la PR 2, mêmes messages).
- Ordre des écritures, après toute validation: sélection appareil/app, `SetTimelineGraphUseCase(view == graph)`, `RequestTimelineFocusUseCase`, `RequestDesktopScreenUseCase(FloconScreen.Timeline)`.

- [ ] **Step 1: tests**: `an unknown event changes nothing`, `a window without events changes nothing`, `the selection, the graph mode, the focus and the screen are requested in that order`, `an event hidden by the user's filters is reported with the reason and the filters are untouched`, `another device is selected before the focus is requested`, `the message tells the user to go to the Flocon app and says the request is registered`, `list view says only the row is selected`.
- [ ] **Step 2: Run** `./gradlew :mcp:desktopTest --tests '*ShowInTimeline*'`, FAIL; **Step 3: Implement**; **Step 4: Run** `./gradlew :mcp:desktopTest :composeApp:desktopTest ktlintFormat`, PASS.
- [ ] **Step 5: Commit** `feat: [MCP] show_in_timeline tool`

### Task 5: essai réel, docs et nettoyage

- [ ] **Step 1: essai réel** (téléphone + desktop): `get_timeline` pour trouver un événement, puis `show_in_timeline` en mode graphe et en mode liste, avec la timeline fermée, avec un autre écran ouvert, avec un filtre qui cache l'événement. Vérifier par capture que la fenêtre reste en arrière-plan.
- [ ] **Step 2: docs**: `docs/mcp.md`, skill `flocon-mcp` (tableau et consigne « dire à l'utilisateur d'aller voir dans Flocon, dire si l'événement est caché »), instructions de `FloconMcpServer.kt`.
- [ ] **Step 3: nettoyage**: `git rm -r docs/superpowers` et retirer `exclude_docs` de `mkdocs.yml`. `mkdocs build --strict` si possible.
- [ ] **Step 4: Commit** `docs: [MCP] show_in_timeline tool` puis `chore: [MCP] Remove the design specs and plans`

---

## Self-review

- **Couverture du spec (outil 2):** section Timeline, mode Graph, session et événement sélectionnés, graphe centré (Tasks 3 et 4); cible appareil/app (Task 4); filtres non touchés et événement caché signalé (Task 2 et 4); « allez voir dans Flocon » (Task 4); jamais au premier plan (contrainte, vérifié Task 5).
- **Cohérence des noms:** `TimelineFocusRequestDomainModel`, `GraphFocusUiModel`, `TimelineHiddenReason`, `McpSelectionApplier` repris à l'identique.
- **Review Focus:** deux appareils et expiration (Task 1 et 3), filtres cachés (Task 2 et 4), viewport écrasé ou déplacé (Task 3), session ancienne (Task 3), aucun événement (Task 4).
- **À confirmer en lisant le code au démarrage de la tâche:** champs exacts de `TimelineEventRef` et du modèle d'événement, existence d'un harnais de test pour `TimelineViewModel`.
