# Swift: files, preferences, databases, dashboards

Prerequisite: [swift.md](swift.md). Shown without setup: `Documents`, `Application Support`, `Caches`, `tmp` and the app container (Files); `UserDefaults.standard` (Preferences); SQLite files in `Documents` and `Library/Application Support` (Databases). Everything else must be registered.

## Files

```swift
startFlocon { flocon in
    flocon.install(.files) { files in
        files.appGroup("group.com.example.app")
        files.folder(displayName: "Exports", url: exportsURL)
    }
}
// or later, for folders known after start
Flocon.filesPlugin.registerAppGroup("group.com.example.app")
Flocon.filesPlugin.register(displayName: "Exports", url: exportsURL)
```

Find the App Groups in the `.entitlements` files (`com.apple.security.application-groups`), and the folders created with `containerURL(forSecurityApplicationGroupIdentifier:)`.

## Preferences

```swift
flocon.install(.preferences) { prefs in
    prefs.userDefaults(UserDefaults(suiteName: "group.com.example.app")!, suiteName: "group.com.example.app")
}
// or later
Flocon.preferencesPlugin.registerUserDefaults(sharedDefaults, suiteName: "group.com.example.app")
```

Keychain: only on explicit request, in debug only (values are sent unencrypted to the Mac). Ask read-only vs editable:

```swift
#if DEBUG
Flocon.preferencesPlugin.registerKeychain()                    // read-only
// Flocon.preferencesPlugin.registerKeychain(isEditable: true) // edits from Desktop are applied
#endif
```

A custom store (Realm, a settings service…): implement `FloconPreference` and `Flocon.preferencesPlugin.register(_:)`.

## SQLite / Core Data

Register the real store URLs, especially the ones outside the default folders (App Group, per-account folder):

```swift
let container = NSPersistentContainer(name: "Model")
container.loadPersistentStores { description, error in
    #if DEBUG
    if error == nil, let url = description.url {
        Flocon.databasePlugin.register(displayName: "Model", url: url)
    }
    #endif
}
```

- Stores created per account or only after login: register them when they are opened (after login), not at app start. A file that does not exist yet is not listed.
- Two different features: **inspection** (Desktop opens its own SQLite connection to query the file; what registration gives) and **query trace** (the SQL the app runs, logged with `Flocon.databasePlugin.logQuery(database:sqlQuery:bindArgs:)` from the library's trace hook, e.g. GRDB `db.trace`). Core Data has no public per-query hook: do not announce query traces for Core Data when only inspection is installed.

## Dashboards

`Flocon.dashboardPlugin.dashboard(id:) { … }` returns a `FloconDashboardHandle`. Keep it (a stored property of a long-lived object) for a dashboard with reactive sections (`FloconSection(_:publisher:)`): when it is released the dashboard stops following its publishers. A fully static dashboard does not need it. Actions run on the main actor.
