# MCP: afficher une section et changer la cible de Flocon (PR 2)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Un outil MCP `floconNavigateIntoDesktopApp` qui, depuis l'agent, affiche n'importe quelle section de Flocon Desktop, change l'appareil ou l'app sélectionnés et règle le mode Graph/List de la timeline, puis dit à l'utilisateur d'aller voir dans Flocon.

**Architecture:** Un conteneur de demande d'écran en mémoire vit dans `domain` (le module `mcp` et `composeApp` ne se voient pas). L'outil y écrit; `AppViewModel` l'observe, navigue via son `onSelectMenu` existant, puis consomme la demande. Le changement d'appareil/app et le mode Graph passent directement par les cas d'usage existants (`SelectDeviceUseCase`, `SelectDeviceAppUseCase`, `SetTimelineGraphUseCase`), sans UI.

**Tech Stack:** Kotlin Multiplatform, Koin, MCP Kotlin SDK, tests `commonTest` (`domain`, `composeApp`) et `desktopTest` (`mcp`).

**Spec:** `docs/superpowers/specs/2026-11-03-mcp-timeline-design.md` (section « Découpage en trois PR », PR 2, et « Outil 2 »).

## Global Constraints

- Flocon ne passe **jamais** au premier plan: l'outil enregistre une demande et la réponse dit à l'utilisateur d'aller voir dans Flocon. Aucune garantie que la fenêtre soit visible.
- Une demande en attente est **remplacée** par la suivante et **expire après 10 minutes**.
- Le choix List/Graph est **mémorisé** dans les préférences: l'écrasement est accepté (pas d'état temporaire).
- Si l'agent vise un autre appareil ou une autre app, l'outil **change la sélection de l'utilisateur**.
- Générique: toutes les sections sauf `Settings`. Pas de mise en évidence d'un événement (c'est la PR 3).
- Le protocole mobile ⇄ desktop n'est pas modifié.
- Règles du dépôt: `mcp` ne dépend pas de `composeApp`; chaque classe est enregistrée dans le `DI.kt` de sa couche; l'outil a ses tests dans `mcp/src/desktopTest`; `docs/mcp.md` et `plugins/flocon/skills/flocon-debug/SKILL.md` sont mis à jour dans la même PR.
- JDK 21 pour Gradle, depuis `FloconDesktop/`. Les tâches de test des modules KMP du desktop s'appellent `desktopTest` (pas `jvmTest`); `--tests` ne marche pas sur `allTests`.
- Branche `feat/mcp-navigate-desktop-app`, commits au format `feat: [MCP] <résumé>`, pas de ligne `Co-Authored-By`.

## Review Focus

- Aucun paramètre fourni: erreur claire, rien n'est modifié.
- Un `deviceId` ou `packageName` inconnu: erreur, et **aucun changement partiel** (ni écran, ni mode Graph).
- `packageName` seul: on cherche l'app comme le fait `McpTargetResolver`; l'appareil change si l'app est sur un autre.
- `Settings` n'est pas joignable par l'outil.
- Une demande expirée (plus de 10 minutes) est ignorée; une demande plus récente remplace l'ancienne; une demande consommée ne rejoue pas.
- Chaque `SubScreen` (hors `Settings`) a un `FloconScreen` correspondant, et réciproquement, sinon une nouvelle section serait inaccessible au MCP sans que personne ne le voie.

---

## File Structure

- `domain/src/commonMain/kotlin/io/github/openflocon/domain/screen/models/FloconScreen.kt`: enum des sections joignables.
- `domain/src/commonMain/kotlin/io/github/openflocon/domain/screen/ScreenRequestStore.kt` + `DI.kt`: demande en attente (remplacement, expiration, consommation).
- `composeApp/.../app/ScreenMapper.kt`: `FloconScreen` → `SubScreen`, et `AppViewModel.kt` qui l'observe.
- `mcp/.../mcp/screen/NavigateDesktopAppMcpToolProvider.kt`, `NavigateDesktopAppMcpInputs.kt`, `NavigateDesktopAppMcpModels.kt`: l'outil.
- Docs: `docs/mcp.md`, `plugins/flocon/skills/flocon-debug/SKILL.md`.

### Task 1: la demande d'écran dans le domaine

**Files:**
- Create: `FloconDesktop/domain/src/commonMain/kotlin/io/github/openflocon/domain/screen/models/FloconScreen.kt`
- Create: `FloconDesktop/domain/src/commonMain/kotlin/io/github/openflocon/domain/screen/ScreenRequestStore.kt`
- Create: `FloconDesktop/domain/src/commonMain/kotlin/io/github/openflocon/domain/screen/DI.kt` (et l'inclure depuis le `DI.kt` parent du domaine)
- Test: `FloconDesktop/domain/src/commonTest/kotlin/io/github/openflocon/domain/screen/ScreenRequestStoreTest.kt`

**Interfaces:**
- Produces:
  - `enum class FloconScreen { Dashboard, Network, Images, Database, Files, SharedPreferences, Analytics, FeatureFlags, Accessibility, Tables, Deeplinks, AdbCommander, CrashReporter, Timeline, WebView }` (tout `SubScreen` sauf `Settings`).
  - `class ScreenRequestStore(now: () -> Long = { Clock.System.now().toEpochMilliseconds() })` avec:
    - `val pending: StateFlow<ScreenRequest?>`
    - `fun request(screen: FloconScreen)`: remplace la demande en attente.
    - `fun consume(request: ScreenRequest)`: l'efface seulement si c'est encore la même.
  - `data class ScreenRequest(val screen: FloconScreen, val requestedAtMs: Long)`
  - `const val SCREEN_REQUEST_TTL_MS = 600_000L` (10 minutes): `pending` n'émet jamais une demande plus vieille que cela.

- [ ] **Step 1: Write the failing tests** dans `ScreenRequestStoreTest` avec une horloge manuelle (`var time = 0L; ScreenRequestStore { time }`): `a request becomes pending`, `a newer request replaces the pending one`, `consuming the pending request clears it`, `consuming an older request does not clear a newer one`, `a request older than the ttl is not pending anymore` (avancer l'horloge de `SCREEN_REQUEST_TTL_MS + 1`, puis lire `pending.value`), `a request exactly at the ttl is still pending`.
- [ ] **Step 2: Run** `./gradlew :domain:allTests`. Attendu: échec de compilation (classes inexistantes).
- [ ] **Step 3: Implement** les trois fichiers. `pending` est dérivé de l'état interne et de `now()`: la lecture d'une demande expirée donne `null`. Choisir un `StateFlow` en lecture qui vérifie l'expiration à la lecture (par exemple une `MutableStateFlow` interne plus un getter qui filtre), tant que `pending.value` reste cohérent avec l'horloge. Enregistrer `ScreenRequestStore` en `single` dans `screen/DI.kt`.
- [ ] **Step 4: Run** `./gradlew :domain:allTests`. Attendu: PASS.
- [ ] **Step 5: Commit** `feat: [MCP] Domain store of the screen the Flocon window is asked to display`

### Task 2: l'app affiche l'écran demandé

**Files:**
- Create: `FloconDesktop/composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/app/ScreenMapper.kt`
- Modify: `FloconDesktop/composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/app/AppViewModel.kt` (nouveau paramètre `screenRequestStore: ScreenRequestStore`; un bloc `init` à côté de celui de `menuNavigator`, ligne ~143)
- Test: `FloconDesktop/composeApp/src/commonTest/kotlin/io/github/openflocon/flocondesktop/app/ScreenMapperTest.kt`

**Interfaces:**
- Consumes: `ScreenRequestStore.pending` / `consume`, `FloconScreen` (Task 1).
- Produces: `internal fun FloconScreen.toSubScreen(): SubScreen`.

- [ ] **Step 1: Write the failing tests** dans `ScreenMapperTest`: `every FloconScreen maps to a SubScreen` (parcourt `FloconScreen.entries`), `every SubScreen but Settings has a FloconScreen` (liste des `SubScreen` connus écrite dans le test, avec un commentaire demandant de la compléter à l'ajout d'une section; vérifie que l'image réciproque couvre tout sauf `SubScreen.Settings`), `Settings is not reachable` (aucun `FloconScreen` ne donne `SubScreen.Settings`).
- [ ] **Step 2: Run** `./gradlew :composeApp:desktopTest`. Attendu: échec de compilation.
- [ ] **Step 3: Implement** `toSubScreen()` avec un `when` exhaustif, puis dans `AppViewModel` un `viewModelScope.launch(dispatcherProvider.viewModel)` qui collecte `screenRequestStore.pending`, ignore `null`, appelle `onSelectMenu(AppAction.SelectMenu(request.screen.toSubScreen()))` puis `screenRequestStore.consume(request)`. Mettre à jour le Koin du `AppViewModel` si nécessaire.
- [ ] **Step 4: Run** `./gradlew :composeApp:desktopTest`. Attendu: PASS.
- [ ] **Step 5: Run** `./gradlew ktlintFormat`, puis relancer le Step 4.
- [ ] **Step 6: Commit** `feat: [MCP] The app displays the screen requested through the domain store`

### Task 3: l'outil `floconNavigateIntoDesktopApp`

**Files:**
- Create: `FloconDesktop/mcp/src/desktopMain/kotlin/io/github/openflocon/mcp/screen/NavigateDesktopAppMcpToolProvider.kt`, `NavigateDesktopAppMcpInputs.kt`, `NavigateDesktopAppMcpModels.kt`
- Modify: `FloconDesktop/mcp/src/desktopMain/kotlin/io/github/openflocon/mcp/DI.kt` (une ligne `singleOf(::NavigateDesktopAppMcpToolProvider) bind McpToolProvider::class`)
- Modify: `FloconDesktop/mcp/src/desktopTest/kotlin/io/github/openflocon/mcp/Fakes.kt` (`FakeDevicesRepository.selectDevice` / `selectApp` enregistrent la sélection au lieu de `error("not used")`)
- Modify: `docs/mcp.md`, `plugins/flocon/skills/flocon-debug/SKILL.md`
- Test: `FloconDesktop/mcp/src/desktopTest/kotlin/io/github/openflocon/mcp/screen/NavigateDesktopAppMcpToolProviderTest.kt`

**Interfaces:**
- Consumes: `ScreenRequestStore`, `FloconScreen` (Task 1); `McpTargetResolver.resolve(deviceId, packageName)`; `SelectDeviceUseCase`, `SelectDeviceAppUseCase`, `SetTimelineGraphUseCase`.
- Produces: l'outil `floconNavigateIntoDesktopApp` (annotations `writeToolAnnotations(destructive = false, idempotent = true)`).
  - Entrée `NavigateDesktopAppInput(screen: String? = null, deviceId: String? = null, packageName: String? = null, timelineView: String? = null)`. `screen`: nom en `snake_case` d'un `FloconScreen` (ex. `feature_flags`, `adb_commander`); `timelineView`: `graph` ou `list`. Ici `deviceId` et `packageName` désignent la **cible à sélectionner dans Flocon**; ils ne reprennent pas `McpTargetArguments`, dont le sens (« viser sans changer ») diffère, et leur `@McpDescription` le dit.
  - Sortie `NavigateDesktopAppMcpResult(applied: List<String>, message: String)`. `message` se termine par une consigne à l'utilisateur d'aller voir dans Flocon (la fenêtre n'est pas passée au premier plan).

- [ ] **Step 1: Write the failing tests** dans `NavigateDesktopAppMcpToolProviderTest` (même squelette que `FeatureFlagsMcpToolProviderTest`: `McpTestServer`, `FakeDevicesRepository`, un `ScreenRequestStore` réel, un faux `TimelinePreferencesRepository` enregistrant `setGraph`). Noms:
  - `the tool is not read only and is idempotent`
  - `without any parameter the tool answers an error and changes nothing`
  - `screen requests the screen in the store` (`feature_flags` → `FloconScreen.FeatureFlags`)
  - `an unknown screen is an error listing the valid ones`
  - `settings cannot be requested`
  - `timelineView graph sets the graph mode, list unsets it`
  - `an invalid timelineView is an error and changes nothing`
  - `deviceId and packageName select that device then that app`
  - `an unknown device or app is an error and nothing else is applied` (écran demandé en même temps: le store reste vide, le mode Graph n'est pas modifié)
  - `packageName alone selects the app, and its device if it is on another one`
  - `the answer lists what was applied and tells the user to go to Flocon`
- [ ] **Step 2: Run** `./gradlew :mcp:desktopTest --tests '*NavigateDesktopAppMcpToolProviderTest*'`. Attendu: échec de compilation.
- [ ] **Step 3: Implement** le fournisseur. Ordre: valider tous les paramètres d'abord (écran connu et hors `Settings`, `timelineView` valide, cible résolue via `McpTargetResolver`) et lever `McpToolException` avant toute écriture; au moins un paramètre exigé; puis appliquer dans l'ordre sélection appareil, sélection app, mode Graph, demande d'écran. `applied` décrit chaque action faite. Étendre `Fakes.kt` comme indiqué.
- [ ] **Step 4: Run** `./gradlew :mcp:desktopTest`. Attendu: PASS, y compris `FloconMcpServerTest` et les tests existants.
- [ ] **Step 5: Docs.** Ajouter `floconNavigateIntoDesktopApp` (paramètres, effet de bord sur la sélection de l'utilisateur et sur le mode Graph, consigne « allez voir dans Flocon ») à `docs/mcp.md` et à `plugins/flocon/skills/flocon-debug/SKILL.md`. Run `mkdocs build --strict`. Attendu: succès.
- [ ] **Step 6: Run** `./gradlew ktlintFormat`, puis `./gradlew :domain:allTests :composeApp:desktopTest :mcp:desktopTest`. Attendu: PASS.
- [ ] **Step 7: Commit** `feat: [MCP] floconNavigateIntoDesktopApp tool to display a screen and change the selected device/app`

---

## Self-review

- **Couverture du spec (PR 2):** affichage de n'importe quelle section sauf `Settings` (Tasks 1-3), changement d'appareil/app (Task 3), mode Graph/List (Task 3), conteneur dans `domain` avec remplacement et expiration à 10 minutes (Task 1), jamais de premier plan et consigne à l'utilisateur (Task 3). Hors périmètre: sélection d'un événement et centrage du graphe (PR 3).
- **Cohérence des noms:** `FloconScreen`, `ScreenRequest`, `ScreenRequestStore.pending/request/consume`, `toSubScreen()`, `floconNavigateIntoDesktopApp`, `NavigateDesktopAppInput`, `NavigateDesktopAppMcpResult` sont identiques dans toutes les tâches.
- **Review Focus:** chaque ligne a son test (Task 1: remplacement, expiration, consommation; Task 2: exhaustivité et `Settings`; Task 3: aucun paramètre, cible inconnue sans changement partiel, `packageName` seul).
- **Proportion:** le plan décide les signatures, les noms de test et les règles du spec; il laisse l'implémentation des corps au développeur.
