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

## Notes

- Room Database n'est disponible que sur Android
- SharedPreferences n'est disponible que sur Android
- L'UI Compose est 100% partagée entre Android et Desktop

