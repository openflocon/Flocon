# MCP timeline: `get_timeline` (PR 3a)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Un agent comprend ce que l'app a fait à un moment donné (récemment, cette session, avant le crash, à une heure) en un seul appel: un récit condensé de la timeline, anomalies en tête, toutes sources confondues.

**Architecture:** La requête de la timeline accepte des bornes de temps et une liste d'événements (SQL, jamais en mémoire). Un use case lit la fenêtre. Dans `mcp`, deux classes pures et testées sans serveur (`TimelineWindowResolver` choisit la fenêtre, `TimelineNarrator` regroupe, détecte les anomalies et plafonne) sont branchées par un `McpToolProvider`. Le protocole mobile ⇄ desktop n'est pas touché.

**Tech Stack:** Kotlin Multiplatform, Room (requête brute), MCP Kotlin SDK (`addTypedTool`), tests `commonTest` (`:data:local`, `:domain`) et `desktopTest` (`:mcp`).

**Spec:** `docs/superpowers/specs/2026-11-03-mcp-timeline-design.md` (outil 1). Le choix du `planner` (noms, seuils) est repris ici. Dépend de la PR 1 (mergée: `responseByteSize`) et de la PR 2 (`flocon_navigate_into_desktop_app`, pour la phrase « allez voir dans Flocon »).

## Global Constraints

- Outil nommé `get_timeline`, en snake_case comme les autres. Annotations `readOnlyToolAnnotations`: l'outil ne modifie ni l'UI ni les préférences.
- Le module `mcp` ne dépend pas de `composeApp`.
- Les bornes de temps et les références d'événements sont appliquées **dans la requête SQL**.
- Les heures des événements sont celles du téléphone: « récemment » et la fenêtre par défaut se mesurent depuis le **dernier événement**, jamais depuis l'horloge du desktop.
- Heure sans date (`HH:mm[:ss]`): fuseau local du desktop (`TimeZone.currentSystemDefault()`), dernière occurrence passée (aujourd'hui, sinon hier).
- Constantes (valeurs exactes): fenêtre crash = [crash − 60 s, crash + 5 s]; sans crash = les 2 dernières minutes de la session la plus récente; `aroundSeconds` = 30; `minutes` (mode `recent`) = 5; `maxLines` = 150 (max 400); regroupement à partir de 3 événements consécutifs, écart ≤ 2 s; boucle = même clé réseau ≥ 5 fois en 10 s; appel volumineux = `responseByteSize >= 1_048_576`; au plus 20 anomalies et 50 `searchMatches`.
- Protocole inchangé; pas de nouvelle dépendance.
- Commits `feat: [MCP] <résumé>`; branche `feat/mcp-timeline-read` depuis `main` une fois la PR 2 mergée; PR en brouillon avec le modèle du dépôt.
- JDK 21 pour Gradle, depuis `FloconDesktop/`.

## Review Focus

- Aucun événement, aucune session (`sessionStart` null), aucun appareil sélectionné: un message clair, jamais un récit vide.
- Crash en bord de session: le crash est dans la session précédente et le redémarrage a créé une nouvelle session (la fenêtre par défaut doit l'attraper); heure demandée dans un trou entre deux sessions.
- Très grande fenêtre ou beaucoup d'événements: les bornes sont en SQL; le plafond ne coupe jamais un résultat de `search`; un message dit si les logs base de données peuvent être partiels (la requête ne remonte que les 20 000 plus récents).
- Deux appareils: l'outil suit la convention `McpTargetArguments`; un `deviceId` inconnu donne la liste des couples valides.
- Une erreur HTTP, un crash ou un appel volumineux ne se perd jamais dans un groupe: les erreurs ne sont jamais repliées avec des succès.

---

## File Structure

- `domain/.../timeline/models/TimelineEventRef.kt` (nouveau) et `TimelineFilterDomainModel.kt` (champs `fromMs`, `toMs`, `events`).
- `data/local/.../timeline/datasource/TimelineQueryBuilder.kt`: filtres de temps et de références.
- `domain/.../timeline/usecase/GetTimelineEventsUseCase.kt` (nouveau) + `domain/.../timeline/DI.kt`.
- `mcp/.../timeline/TimelineWindowResolver.kt`, `TimelineNarrator.kt`, `TimelineMcpToolProvider.kt`, `TimelineMcpInputs.kt`, `TimelineMcpModels.kt` (nouveaux), `mcp/DI.kt`, `FloconMcpServer.kt` (instructions).
- Tests: `TimelineQueryBuilderTest`, `GetTimelineEventsUseCaseTest`, `TimelineWindowResolverTest`, `TimelineNarratorTest`, `TimelineMcpToolProviderTest` (+ `FakeTimelineRepository` dans `Fakes.kt`).
- Docs: `docs/mcp.md`, `plugins/flocon/skills/flocon-mcp/SKILL.md`.

### Task 1: bornes de temps et références dans la requête

**Files:** les `data/local` et `domain` ci-dessus; test `TimelineQueryBuilderTest.kt`.

**Interfaces:**
- Produces: `TimelineFilterDomainModel` gagne `fromMs: Long? = null`, `toMs: Long? = null`, `events: Set<TimelineEventRef>? = null`. `data class TimelineEventRef(val source: TimelineSource, val id: Long)` (champs à confirmer sur le modèle d'événement existant: `kind`/`id`). `GetTimelineEventsUseCase(repository)` expose `suspend operator fun invoke(target: DeviceIdAndPackageNameDomainModel, filter: TimelineFilterDomainModel): List<TimelineEventDomainModel>` = `observeTimelineEvents(...).first()`.

- [ ] **Step 1: tests** dans `TimelineQueryBuilderTest`: `time bounds add a where on the event time`, `an event list restricts to the kind and id pairs`, `without bounds nor events the query is unchanged` (comparer au SQL actuel).
- [ ] **Step 2: Run** `./gradlew :data:local:desktopTest --tests '*TimelineQueryBuilderTest*'`. Attendu: FAIL.
- [ ] **Step 3: Implement** un `WHERE` externe sur `u.ts` et sur `u.kind`/`u.id`; une référence cible l'événement « requête » pour le réseau. Ajouter `GetTimelineEventsUseCase` et l'enregistrer (`factoryOf`).
- [ ] **Step 4: Run** `./gradlew :data:local:desktopTest :domain:desktopTest`. Attendu: PASS.
- [ ] **Step 5: Commit** `feat: [MCP] Time bounds and event refs in the timeline query`

### Task 2: choix de la fenêtre (pur)

**Files:** `mcp/src/desktopMain/.../timeline/TimelineWindowResolver.kt`; test `mcp/src/desktopTest/.../timeline/TimelineWindowResolverTest.kt`.

**Interfaces:**
- Produces: `class TimelineWindowResolver(clock: () -> Long, zone: TimeZone)` avec `fun resolve(input: GetTimelineInput, sessions: List<SessionInfo>, crashes: List<CrashInfo>, lastEventMs: Long?): TimelineWindow`; `data class TimelineWindow(fromMs, toMs, reason: String, anchorEventId: String?)`. `SessionInfo`/`CrashInfo` sont de petits types locaux (début de session, instant du crash, message).
- Consumes: `GetTimelineInput` (Task 3), les `sessionStarts` du modèle de domaine.

- [ ] **Step 1: tests** (un par ligne de Review Focus qui concerne la fenêtre): `no input anchors on the crash of the latest session`, `a crash just before the latest session anchors the window when that session started within 60 s of it`, `no crash gives the last 2 minutes of the latest session`, `recent is measured from the last event, not from the clock`, `HH:mm resolves to its latest past occurrence`, `an instant in a gap between sessions keeps the requested window and says so`, `no events gives a window with a reason, not an exception`.
- [ ] **Step 2: Run** `./gradlew :mcp:desktopTest --tests '*TimelineWindowResolverTest*'`. Attendu: FAIL.
- [ ] **Step 3: Implement.** `at`/`from`/`to` acceptent epoch ms, ISO-8601 avec offset, ou `HH:mm[:ss]`. Valeur invalide: `McpToolException` avec le format attendu. `reason` dit toujours pourquoi (« anchored on the crash X at 14:32:05 »).
- [ ] **Step 4: Run** la commande du Step 2. Attendu: PASS.
- [ ] **Step 5: Commit** `feat: [MCP] Window of the timeline read tool`

### Task 3: récit condensé (pur) et outil

**Files:** `TimelineNarrator.kt`, `TimelineMcpInputs.kt`, `TimelineMcpModels.kt`, `TimelineMcpToolProvider.kt`, `mcp/DI.kt`, `Fakes.kt`; tests `TimelineNarratorTest.kt`, `TimelineMcpToolProviderTest.kt`.

**Interfaces:**
- Produces: `GetTimelineInput : McpTargetArguments` avec `mode` (`recent|session|before_crash|at|range`, `@SerialName`, optionnel), `at`, `from`, `to`, `eventId`, `aroundSeconds`, `minutes`, `search`, `searchSource`, `maxLines`, `deviceId`, `packageName`. `TimelineNarrator.narrate(events, window, search, maxLines): TimelineNarrative` (`lines`, `anomalies`, `searchMatches`, `found`, `truncated`, `omittedCount`). Résultat de l'outil: `window { fromMs, toMs, fromLocal, toLocal, timeZone, reason }` + le récit.
- Identifiant de ligne: `<source en minuscules>:<id>` (`network:<callId>`, `crash:<crashId>`...), avec l'outil de détail à utiliser (`get_network_call`, `get_crash_report`; sinon le `list_*` de la source).
- Ligne: `+320 ms  14:32:05.120  NET  GET /api/feed  500  1.2 s  3.4 MB  [network:abc]`; l'écart est depuis la ligne précédente gardée (ms sous 1 s, puis s, puis min); les événements réseau « réponse » sont écartés (une ligne par appel).
- Regroupement: clé réseau = méthode + chemin normalisé (requête supprimée, segments numériques ou UUID remplacés par `:id`); analytics = nom de l'événement; autres = titre. Sortie `12× GET /api/feed in 3.1 s [network:first … network:last]`.

- [ ] **Step 1: tests du `TimelineNarrator`**: `three consecutive identical calls within 2 s fold into one line`, `an error is never folded with successes`, `5 identical calls in 10 s are flagged as a loop`, `a response over 1 MB is flagged as large`, `an image over 1 MB is labelled as an image`, `search matches survive the cap and are listed with found = true`, `a search with no match gives found = false`, `the cap reports truncated and omittedCount`, `anomalies come first and are limited to 20`, `lines near the anchor are kept first when truncating`.
- [ ] **Step 2: tests du tool provider** (`McpTestServer`, `FakeTimelineRepository`): `get_timeline without input anchors on the crash`, `eventId anchors the window`, `search answers whether an analytics event was sent`, `an unknown device lists the valid pairs`, `no selected device gives a clear message`, `the answer carries the window and the reason`.
- [ ] **Step 3: Run** `./gradlew :mcp:desktopTest --tests '*Timeline*'`. Attendu: FAIL.
- [ ] **Step 4: Implement** le narrateur (pur, sans accès base) puis le provider: résout la cible (`McpTargetResolver`), charge crashs et sessions, appelle le résolveur, lit via `GetTimelineEventsUseCase` avec bornes et `events`, narre, répond en JSON (`McpJson`). Enregistré dans `mcp/DI.kt`. Description de l'outil: heures du téléphone, lire les événements par id avec les outils de détail.
- [ ] **Step 5: Run** `./gradlew :mcp:desktopTest :domain:desktopTest :data:local:desktopTest ktlintFormat`. Attendu: PASS.
- [ ] **Step 6: Commit** `feat: [MCP] get_timeline tool`

### Task 4: documentation

**Files:** `docs/mcp.md`, `plugins/flocon/skills/flocon-mcp/SKILL.md`, `FloconMcpServer.kt` (instructions).

- [ ] **Step 1:** ajouter `get_timeline` à `docs/mcp.md` (modes, fenêtre par défaut, exemple de réponse, heures du téléphone), au tableau et aux consignes du skill (« avant d'enchaîner les `list_*`, commencer par `get_timeline` »), et une ligne dans les instructions du serveur.
- [ ] **Step 2: Run** `mkdocs build --strict` si possible (sinon le dire dans la PR; la CI le fait).
- [ ] **Step 3: Commit** `docs: [MCP] get_timeline tool`

---

## Self-review

- **Couverture du spec (outil 1):** fenêtre par défaut et mode explicite (Task 2), récit regroupé avec anomalies, écarts, plafond et recherche d'un événement (Task 3), « la fenêtre retenue est toujours dite » (Task 3), appels volumineux via `responseByteSize` (Task 3).
- **Cohérence des noms:** `GetTimelineInput`, `TimelineWindow`, `TimelineNarrative`, `TimelineEventRef` repris à l'identique entre les tâches.
- **Review Focus:** chaque ligne a son test (Task 2: fenêtre, sessions, trous; Task 3: groupes, plafond, recherche, appareil inconnu).
- **Hors de cette PR:** `show_in_timeline`, centrage du graphe (PR 3b); suppression de `docs/superpowers/` (fin de la PR 3b).
