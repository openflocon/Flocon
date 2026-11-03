# MCP Timeline: conception

Date: 2026-11-03. Statut: en attente d'approbation.

## Intention

Aujourd'hui, un agent qui veut comprendre ce que l'app a fait doit appeler sept outils `list_*` séparés (réseau, analytics, base de données, crashs, deeplinks, WebView, accessibilité), puis recoller lui-même les horodatages, sans notion de session. La timeline du desktop fait déjà cette fusion, mais seulement pour un humain devant l'écran.

On ajoute deux outils MCP, au-dessus du réseau, qui répondent à une seule question: **« que s'est-il passé à ce moment-là ? »**

1. **Lire**: un récit condensé d'une fenêtre de temps, toutes sources confondues.
2. **Montrer**: déplacer l'écran de Flocon pour que l'utilisateur voie la zone concernée.

## Buts

- L'utilisateur ou l'agent donne un repère temporel, jamais une liste de filtres techniques:
  - une heure ou une plage (« 14h32 », « entre 14h30 et 14h35 »);
  - « récemment », « cette session », « avant le crash »;
  - ou rien du tout: l'outil choisit.
- Savoir si un événement donné est parti dans une fenêtre (par exemple un event analytics).
- Mettre en avant les anomalies: crash, erreurs HTTP, appels en boucle, appels volumineux.
- Laisser à l'utilisateur la vue des événements, sans qu'il ait à les décrire à l'agent.

## Hors périmètre

- Le suivi en direct (curseur « depuis mon dernier appel »).
- Reproduire les fonctions d'affichage de l'UI pour un humain: zoom manuel, options d'affichage, compteurs détaillés.
- Dupliquer le détail d'un événement: il reste dans les outils existants (`get_network_call`, `get_crash_report`, plugin analytics…).
- Passer la fenêtre Flocon au premier plan. Jamais.

## Anti-buts (un échec même si ça « marche »)

- Un outil de lecture qui modifie l'UI ou les préférences.
- Une réponse énorme: l'agent noyé dans des centaines de lignes réseau.
- Une fenêtre choisie en silence: l'agent doit toujours savoir laquelle a été retenue.
- Voler le focus de l'utilisateur pendant qu'il travaille ailleurs.

## Contraintes

- Le module `mcp` ne dépend pas de `composeApp`, et `composeApp` ne dépend pas de `mcp`. Le conteneur de demande de focus vit donc dans `domain`, que les deux modules lisent. (`FloconWindowCapturer` est le modèle d'une interface passée au MCP, mais il vit dans le module `mcp`, pas dans `domain`.)
- Le protocole mobile ⇄ desktop n'est pas modifié: tout est déjà côté desktop. À confirmer en lisant le code (voir « Laissé au développeur »).
- Règles du dépôt: tests de chaque outil dans `mcp/src/desktopTest`; mise à jour de `docs/mcp.md` et de `plugins/flocon/skills/flocon-debug/SKILL.md` dans la même PR.

## Découpage en trois PR (décision prise)

Ce document est la conception d'ensemble. Chaque PR aura son propre plan, avec ses tests et sa mise à jour de `docs/mcp.md` et du skill `flocon-debug`.

1. **PR 1: la taille des appels dans la timeline.** On ajoute la taille de la réponse à la requête de la timeline, au modèle de domaine et aux tests, pour pouvoir détecter les appels volumineux. Elle ne touche pas au MCP.
2. **PR 2: naviguer et interagir avec l'app depuis le MCP, de façon générique.** Un outil (ou un petit groupe d'outils) qui affiche **n'importe quelle section** (réseau, base de données, timeline…), change l'appareil ou l'app sélectionnés, et règle le mode Graph/List de la timeline. Elle pose le conteneur de demande de focus dans `domain`. La mise en évidence d'un événement précis (sélection, centrage du graphe) n'en fait pas partie.
3. **PR 3: l'évolution du MCP de timeline.** L'outil de lecture (récit condensé, anomalies, regroupement) et `show_in_timeline` (sélection de l'événement, graphe centré), qui s'appuie sur les PR 1 et 2. Les appels volumineux sont **inclus** (décision prise).

Les PR 1 et 2 sont indépendantes. La PR 3 dépend des deux.

## Approches considérées

Seule la partie « montrer » avait un vrai choix de construction. La lecture réutilise dans tous les cas les cas d'usage existants du domaine (`ObserveTimelineUseCase`, `ObserveTimelineSummaryUseCase`).

- **A. Bus de commandes d'UI.** Le domaine expose un flux d'ordres, que les ViewModels écoutent. Simple, mais l'ordre est perdu si l'écran Timeline n'est pas encore affiché.
- **B. Demande de focus mémorisée (retenue).** Le domaine garde une « demande en attente » (événement, plage, mode graphe) que `AppViewModel` et `TimelineViewModel` consomment une seule fois. Proche de `TimelineFilterStore`. Elle fonctionne quand Flocon est en arrière-plan ou que l'écran n'est pas ouvert, ce qui est le cas courant puisqu'on ne passe jamais Flocon devant.
- **C. Lecture seule d'abord.** Livrable vite, mais perd l'idée « allez voir sur Flocon ».

Choix: **B**, parce que l'app est souvent en arrière-plan quand l'agent agit.

## Conception

### Outil 1: lire (récit condensé)

**Entrée**
- Un repère temporel facultatif: heure ou plage, « récent », « session », « avant le crash », ou un identifiant d'événement comme ancre.
- Le choix de l'appareil, comme les autres outils MCP.

**Choix de la fenêtre quand rien n'est précisé** (décision prise avec l'utilisateur):
1. s'il y a eu un crash dans la session courante, on s'ancre dessus;
2. sinon, on prend les dernières minutes de la session la plus récente.

La réponse indique toujours la fenêtre retenue et pourquoi (« ancré sur le crash de 14h32 »).

**Heure sans date** (« 14h32 »): la session qui contient cette heure, sinon la plus récente passée à cette heure. (Proposition, à confirmer.)

**Sortie**: une ligne par événement, toutes sources confondues, avec un identifiant que l'agent passe aux outils existants pour le détail.
- Les répétitions sont regroupées (« 12 appels GET /feed en 3 s »).
- Les anomalies sont signalées en tête: crash, erreurs HTTP, appels en boucle, appels volumineux.
- L'écart avec l'événement précédent est indiqué (« +320 ms »).
- Le même outil répond à « tel événement est-il parti dans cette fenêtre ? ».
- Un plafond de taille borne la réponse.

### Outil 2: montrer (`show_in_timeline`)

L'outil déplace l'écran de Flocon, sans jamais le passer au premier plan:
1. change la section affichée vers la Timeline, quel que soit le plugin ouvert;
2. règle le mode **Graph** (ou **List**). Ce réglage est mémorisé dans les préférences de l'utilisateur: l'écrasement est accepté (décision prise);
3. positionne la vue: bonne session, événement sélectionné, graphe centré sur la zone (crash, appels en boucle, appels volumineux);
4. répond par une consigne que l'agent relaie: « Rendez-vous sur Flocon, j'ai affiché la timeline en mode graphe sur le crash de 14h32 ».

**Appareil ou app ciblé** (décision prise): si l'agent vise un autre appareil ou une autre app que celui affiché dans Flocon, l'outil **change la sélection de l'utilisateur** vers cette cible. La demande de focus porte donc la cible, et `AppViewModel` change l'appareil avant de naviguer.

**Filtres** (décision prise): l'outil ne touche pas aux filtres de l'utilisateur (recherche, sources cochées, « session courante seulement »). Si l'événement visé est caché par un filtre, la réponse le dit à l'agent (« l'événement existe mais il est masqué par le filtre X ») et l'agent peut le répéter à l'utilisateur. Pour le détecter, l'outil doit pouvoir lire l'état courant des filtres.

Il accepte un événement, une plage, ou « l'anomalie principale ». Si Flocon n'est pas lancé ou si aucune UI n'est connectée, la réponse le dit clairement.

L'agent peut ensuite appeler `take_flocon_desktop_screenshot` pour voir lui-même la zone mise en évidence.

### Mécanisme (approche B)

- Un conteneur d'état dans le domaine contient la demande de focus en attente.
- Le MCP y écrit; `AppViewModel` navigue, `TimelineViewModel` la consomme à l'affichage puis l'efface.
- Le réglage du mode Graph passe par `SetTimelineGraphUseCase`, qui existe déjà.
- Le « centrer le graphe sur une plage de temps » n'existe pas aujourd'hui (le zoom et le déplacement sont manuels) et doit être ajouté.

## Choix faits par Claude (à vérifier à la relecture)

- **Entrées structurées, pas de texte libre.** L'outil de lecture prend un mode (`recent`, `session`, `before_crash`, `at`, `range`) plus des heures ou un identifiant d'événement. C'est l'agent qui traduit « avant le crash » ou « 14h32 ». L'outil est ainsi plus petit et plus testable.
- **Heure sans date**: interprétée dans le fuseau local du desktop.
- **« Cet événement est-il parti ? »** a son propre paramètre de recherche (type, texte), appliqué avant le plafond de taille, pour que le plafond ne coupe jamais l'événement demandé.
- **Une demande de focus en attente** est remplacée par la suivante et expire après un délai court (10 minutes), pour ne pas s'appliquer des heures plus tard sur la mauvaise session. L'outil répond « demande enregistrée, allez voir sur Flocon » : il ne peut pas savoir si la fenêtre est visible.

## Laissé au développeur

- Noms exacts des outils et des paramètres, selon les conventions des outils MCP existants.
- Seuils de détection (appel « volumineux », appels « en boucle ») et plafond de taille de la réponse. À vérifier d'abord que le modèle d'événement contient assez d'informations (taille des réponses, répétitions).
- Forme exacte de la ligne du récit.
- Confirmer que le protocole n'est pas touché.
- Gestion de plusieurs appareils connectés: suivre la convention des autres outils.

## Hors de ce document

Le plan d'implémentation. Il suivra après approbation.
