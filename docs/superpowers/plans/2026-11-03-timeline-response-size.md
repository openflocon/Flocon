# Timeline: taille de la réponse des appels réseau (PR 1)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Les événements réseau de la timeline portent la taille de leur réponse en octets, pour que la PR 3 puisse détecter les appels volumineux.

**Architecture:** La requête SQL de la timeline (`buildTimelineQuery`) est une union de branches qui exposent toutes les mêmes colonnes. On ajoute une colonne `responseByteSize` (lue dans `response_responseByteSize` de `FloconNetworkCallEntity`, `NULL` pour les autres sources), puis on la remonte dans `TimelineRowEntity` et `TimelineEventDomainModel`. Aucune UI ni aucun outil MCP n'est touché.

**Tech Stack:** Kotlin Multiplatform, Room (requête brute), tests `commonTest` du module `:data:local`.

**Spec:** `docs/superpowers/specs/2026-11-03-mcp-timeline-design.md` (section « Découpage en trois PR », PR 1).

## Global Constraints

- Le protocole mobile ⇄ desktop n'est pas modifié: la taille est déjà stockée (`responseByteSize`).
- Pas de seuil « volumineux » ici: il est laissé à la PR 3.
- Pas de changement visible dans l'UI de la timeline.
- JDK 21 pour Gradle (le JDK 22 par défaut casse `compileKotlinDesktop`). Depuis `FloconDesktop/`.
- Commit au format `feat: [TIMELINE] <résumé impératif>`. Branche `feat/timeline-response-size`, jamais `claude/...`. Pas de ligne `Co-Authored-By`.
- Le nouveau comportement a son test (règle du dépôt); aucun test n'est désactivé.

## Review Focus

- Un appel sans réponse encore reçue: la taille est `null`, pas `0`.
- Une réponse vide (corps de 0 octet): la taille est `0`, pas `null`.
- Une taille supérieure à `Int.MAX_VALUE` (plus de 2 Go) ne déborde pas: type `Long`.
- L'événement « réponse » d'un appel porte la taille, comme l'événement « requête ».
- Les sources autres que le réseau ont toujours une taille `null`, et toutes les branches de l'union gardent le même nombre de colonnes (sinon la requête échoue à l'exécution).

---

## File Structure

- `data/local/.../timeline/datasource/TimelineQueryBuilder.kt`: ajoute la colonne à l'union.
- `data/local/.../timeline/models/TimelineRowEntity.kt`: champ `responseByteSize`.
- `data/local/.../timeline/mapper/Mapper.kt`: recopie vers le domaine.
- `domain/.../timeline/models/TimelineEventDomainModel.kt`: champ `responseByteSize`.
- Tests: `data/local/src/commonTest/.../timeline/datasource/TimelineQueryBuilderTest.kt` et un nouveau `.../timeline/mapper/MapperTest.kt`.

### Task 1: la colonne dans la requête

**Files:**
- Modify: `FloconDesktop/data/local/src/commonMain/kotlin/io/github/openflocon/data/local/timeline/datasource/TimelineQueryBuilder.kt` (constantes `NO_NETWORK_COLUMNS` et `EMPTY_ROWS`, branches réseau « requête » et « réponse », SELECT externe de l'union)
- Test: `FloconDesktop/data/local/src/commonTest/kotlin/io/github/openflocon/data/local/timeline/datasource/TimelineQueryBuilderTest.kt`

**Interfaces:**
- Produces: la requête expose une colonne `responseByteSize` après `hasError` et avant `sessionStart`: `u.hasError, u.responseByteSize, (SELECT MAX…) AS sessionStart`. Valeur `response_responseByteSize` pour les branches réseau, `NULL AS responseByteSize` ailleurs.

- [ ] **Step 1: Write the failing test** dans `TimelineQueryBuilderTest`, `fun \`the network calls carry the size of their response, the other events never do\``. Avec `build()` (toutes sources), vérifier que `query.sql` contient `response_responseByteSize AS responseByteSize` (deux fois: branche requête et branche réponse), `NULL AS responseByteSize` et `u.responseByteSize`. Même style que le test existant `the network calls tell if their response is an image…` (lignes 62-70).
- [ ] **Step 2: Run** `cd FloconDesktop && ./gradlew :data:local:allTests --tests '*TimelineQueryBuilderTest*'`. Attendu: FAIL sur le nouveau test.
- [ ] **Step 3: Implement.** Ajouter `NULL AS responseByteSize` à `NO_NETWORK_COLUMNS` et à `EMPTY_ROWS` (avant `sessionStart` pour ce dernier). Dans les deux branches réseau, ajouter `response_responseByteSize AS responseByteSize` juste après la colonne `hasError`. Dans le SELECT externe, ajouter `u.responseByteSize` après `u.hasError`. Mettre à jour le commentaire de la liste des colonnes (ligne ~153) et le test `every member of the union has the same facet columns` s'il compte les colonnes.
- [ ] **Step 4: Run** la commande du Step 2, puis `./gradlew :data:local:allTests`. Attendu: PASS, y compris les tests existants sur l'union.
- [ ] **Step 5: Commit** `feat: [TIMELINE] Expose the response size of the network calls in the timeline query`

### Task 2: remonter la taille jusqu'au domaine

**Files:**
- Modify: `FloconDesktop/data/local/src/commonMain/kotlin/io/github/openflocon/data/local/timeline/models/TimelineRowEntity.kt`
- Modify: `FloconDesktop/data/local/src/commonMain/kotlin/io/github/openflocon/data/local/timeline/mapper/Mapper.kt`
- Modify: `FloconDesktop/domain/src/commonMain/kotlin/io/github/openflocon/domain/timeline/models/TimelineEventDomainModel.kt`
- Test: `FloconDesktop/data/local/src/commonTest/kotlin/io/github/openflocon/data/local/timeline/mapper/MapperTest.kt` (nouveau)

**Interfaces:**
- Consumes: la colonne `responseByteSize` de la Task 1.
- Produces: `TimelineRowEntity.responseByteSize: Long? = null` et `TimelineEventDomainModel.responseByteSize: Long? = null` (octets de la réponse; `null` si ce n'est pas un appel réseau ou si la réponse n'est pas arrivée). Les deux ont une valeur par défaut: les appels existants à ces constructeurs (par exemple `GraphPackingTest`) ne changent pas.

- [ ] **Step 1: Write the failing tests** dans `MapperTest` (`TimelineRowEntity.toDomain()` est `internal`, accessible depuis `commonTest` du même module). Un test par ligne du Review Focus qui concerne la valeur: `a call without response keeps a null size`, `an empty response keeps a size of 0`, `a size over Int.MAX_VALUE is kept` (par exemple `3_000_000_000L`), `the response event carries the size` (`kind = KIND_NETWORK_RESPONSE`), `a database event has no size`. Chaque test construit un `TimelineRowEntity` minimal et vérifie `toDomain().responseByteSize`.
- [ ] **Step 2: Run** `./gradlew :data:local:allTests --tests '*MapperTest*'`. Attendu: échec de compilation (champ inexistant).
- [ ] **Step 3: Implement** `responseByteSize: Long? = null` dans `TimelineRowEntity` (commentaire « network only: size of the response in bytes, null while waiting for it ») et dans `TimelineEventDomainModel`, et la recopie `responseByteSize = responseByteSize` dans `toDomain()`.
- [ ] **Step 4: Run** `./gradlew :data:local:allTests :domain:allTests :composeApp:allTests`. Attendu: PASS (`composeApp` pour `GraphPackingTest` et la compilation des mappers UI).
- [ ] **Step 5: Run** `./gradlew ktlintFormat`, puis relancer le Step 4.
- [ ] **Step 6: Commit** `feat: [TIMELINE] Carry the response size of the network calls up to the domain event`

---

## Self-review

- **Couverture du spec (PR 1):** taille ajoutée à la requête (Task 1), au modèle de données et de domaine (Task 2), avec tests. Pas de MCP, pas d'UI, comme demandé.
- **Cohérence des noms:** `responseByteSize` partout (colonne SQL, `TimelineRowEntity`, `TimelineEventDomainModel`); source `response_responseByteSize`.
- **Review Focus:** chaque ligne a son test (Task 1: colonnes de l'union et événement réponse; Task 2: `null`, `0`, grande valeur, événement réponse, source non réseau).
- **Vérification de fin de PR:** CI lance `./gradlew allTests assemble` sur `FloconDesktop`; en local, les commandes des Steps suffisent (règle `build-verification.md`).
