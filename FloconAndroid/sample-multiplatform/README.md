# App-Multi - Module Kotlin Multiplatform

Ce module est une version multiplateforme de `app` utilisant Kotlin Multiplatform avec Compose Multiplatform.

## Plateformes supportées

- **Android** - Application Android native
- **Desktop (JVM)** - Application Desktop (macOS, Windows, Linux)
- **iOS** - Support iOS (iosX64, iosArm64, iosSimulatorArm64)

## Architecture

### Structure des sources

```
app-multi/
├── src/
│   ├── commonMain/        # Code partagé entre toutes les plateformes
│   │   └── kotlin/
│   │       ├── ui/        # UI Compose Multiplatform
│   │       └── ...        # Logique métier partagée
│   ├── androidMain/       # Code spécifique Android
│   │   └── kotlin/
│   │       └── MainActivity.kt
│   ├── desktopMain/      # Code spécifique Desktop/JVM
│   │   └── kotlin/
│   │       └── Main.kt
│   └── iosMain/          # Code spécifique iOS
```

### Technologies utilisées

- **Kotlin Multiplatform** - Partage de code entre plateformes
- **Compose Multiplatform** - UI déclarative multiplateforme
- **Ktor** - Client HTTP (remplace OkHttp)
- **Room** - Base de données locale (Android uniquement)
- **Flocon** - Intercepteurs et outils de développement

## Changements par rapport au module `app`

✅ **Ajouts:**
- Support Kotlin Multiplatform (Android, JVM, iOS)
- Compose Multiplatform pour UI partagée
- Ktor client avec support multiplateforme

❌ **Retraits:**
- GraphQL (Apollo)
- gRPC
- OkHttp (remplacé par Ktor)

## Lancement des applications

### Android

```bash
# Debug build
./gradlew :app-multi:assembleDebug

# Installer sur appareil/émulateur
./gradlew :app-multi:installDebug

# Lancer depuis IDE
# Ouvrir dans Android Studio et exécuter MainActivity
```

### Desktop (JVM)

```bash
# Exécuter l'application Desktop (depuis le code source)
./gradlew :app-multi:run

# Ou utiliser la tâche spécifique Desktop
./gradlew :app-multi:desktopRun

# Exécuter depuis la distribution packagée
./gradlew :app-multi:runDistributable

# Créer la distribution pour la plateforme actuelle
./gradlew :app-multi:createDistributable

# Créer les packages natifs (DMG, MSI, DEB selon votre OS)
./gradlew :app-multi:packageDistributionForCurrentOS
```

Les distributions packagées seront dans `app-multi/build/compose/binaries/main/`:
- `.dmg` pour macOS
- `.msi` pour Windows
- `.deb` pour Linux

### iOS

```bash
# Compiler pour iOS
./gradlew :app-multi:iosX64MainKlibrary

# Pour exécuter, ouvrir dans Xcode ou utiliser Kotlin Multiplatform plugin
```

## Structure de l'application

L'interface utilisateur est définie dans `commonMain/ui/App.kt` et est utilisée par:
- **Android**: Via `MainActivity.kt` qui initialise le contexte Android
- **Desktop**: Via `Main.kt` qui crée une fenêtre Compose Desktop
- **iOS**: Via le bridge iOS (à configurer)

## Client HTTP Ktor

Le client HTTP est initialisé différemment selon la plateforme:
- **Android**: `OkHttp` engine
- **Desktop**: `CIO` engine
- **iOS**: `CIO` engine

```kotlin
// Exemple d'utilisation
DummyHttpKtorCaller.callGet()  // GET request
DummyHttpKtorCaller.callPost() // POST request
```

## Développement

### Ajouter du code partagé

Placer le code dans `commonMain/kotlin/`. Il sera disponible sur toutes les plateformes.

### Ajouter du code spécifique à une plateforme

- Android: `androidMain/kotlin/`
- Desktop: `desktopMain/kotlin/`
- iOS: `iosMain/kotlin/`

### Expect/Actual

Pour les fonctionnalités spécifiques à une plateforme, utiliser le mécanisme `expect`/`actual`:

```kotlin
// commonMain
expect fun getPlatformName(): String

// androidMain
actual fun getPlatformName() = "Android"

// desktopMain  
actual fun getPlatformName() = "Desktop"
```

## Fonctionnalités testables par plateforme

L'écran commun (`ui/App.kt`) affiche un bouton par fonctionnalité, groupé par plugin. Un bouton **grisé** signifie
que la fonctionnalité n'est pas disponible (ou pas branchée dans le sample) sur la plateforme, la raison est affichée dessous.
Les fonctionnalités sont déclarées dans `ui/SampleFeatures.kt`; chaque point d'entrée (`MainActivity`, `Main.kt`,
`MainViewController`, `main.kt` wasm) ajoute les siennes avec `SampleFeatures.on(...)` ou explique les absentes avec
`SampleFeatures.unavailable(...)`.

| Fonctionnalité | Android | Desktop (JVM) | iOS | Wasm |
| :--- | :---: | :---: | :---: | :---: |
| Ktor GET / POST | ✅ | ✅ | ✅ | ✅ |
| OkHttp, OkHttp gzip, WebSocket | ✅ | ⬜ (sample en Ktor CIO) | ⬜ | ⬜ |
| GraphQL | ✅ | ⬜ | ⬜ | ⬜ |
| gRPC | ✅ | ⬜ | ⬜ | ⬜ |
| Analytics (table, événements) | ✅ | ✅ | ✅ | ✅ |
| Tables | ✅ | ✅ | ✅ | ✅ |
| Dashboard | ✅ | ✅ | ✅ | ✅ |
| Feature flags | ✅ | ✅ | ✅ | ✅ |
| Deeplink enregistré après l'install | ✅ | ✅ | ✅ | ✅ |
| Base de données Room 3 | ✅ | ✅ | ✅ | ✅ (`WebWorkerSQLiteDriver`, bases en mémoire dans le worker du sample) |
| Base de données Room 2 | ✅ | ⬜ (module supporté, pas de base Room 2 dans le sample) | ⬜ (idem) | ⬜ (Room 2 ne supporte pas Wasm) |
| Base de données en mémoire (Room 3) | ✅ | ⬜ | ⬜ | ⬜ |
| Préférences (store en mémoire) | ✅ | ✅ | ✅ | ✅ |
| DataStore | ✅ | ⬜ | ⬜ | ⬜ |
| Fichiers | ✅ | ✅ | ✅ | ⬜ (pas de navigation de fichiers) |
| Crash reporter | ✅ | ✅ | ✅ | ⬜ |

Les plugins Device (icône, redémarrage) et Files sont pilotés depuis l'application desktop: il n'y a rien à
déclencher côté sample, ils sont simplement installés.

## Notes

- Les bases Room 3 sont partagées entre Android, Desktop et iOS (`commonMain/database`); la base Room 2 (`androidMain/room2`) est propre à Android
- Les SharedPreferences Android sont détectées automatiquement; les autres plateformes utilisent le store en mémoire `SamplePreferences`
- L'UI Compose est 100% partagée entre toutes les plateformes
