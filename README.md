<img width="100" height="100" alt="flocon_small" src="https://github.com/user-attachments/assets/27143843-fce2-4c74-96d8-a0b35a8fccde" />     

**Flocon** is an advanced debugging and inspection tool built with **Kotlin Multiplatform (KMP)**, designed to work seamlessly across Android and desktop environments.  

Inspired from [Flipper](https://github.com/facebook/flipper) by Meta, while leveraging **modern Kotlin multiplatform architecture** for networking, databases, analytics, and UI data visualization.

---

## 📚 Documentation

**[Read the full documentation here](docs/index.md)** containing:
- [Setup & Installation](docs/setup.md)
- [Network Inspector](docs/network.md)
- [Database Inspector](docs/database.md)
- [Analytics](docs/analytics.md)
- and much more!

---

**Works on**
|   | Android | Desktop (jvm) | iOS (simulator) | iOS (device) | wasm |
|---|:---:|:---:|:---:|:---:|:---:|
| Network | ✅ | ✅ | ✅ | ❌ | ❌ |
| Database | ✅ | ✅ | ✅ | ❌ | ❌ |
| Preference | ✅ | ❌ | ❌ | ❌ | ❌ |
| Table | ✅ | ✅ | ✅ | ❌ | ❌ |
| Analytics | ✅ | ✅ | ✅ | ❌ | ❌ |
| Deeplink | ✅ | ❌ | ❌ | ❌ | ❌ |
| Files | ✅ | ❌ | ❌ | ❌ | ❌ |
| Images | ✅ | ✅ | ✅ | ❌ | ❌ |
| Dashboards | ✅ | ❌ | ❌ | ❌ | ❌ |

## 🛠️ Quick Setup

in your module .kts

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon.svg)](https://search.maven.org/artifact/io.github.openflocon/flocon)
```kotlin
debugImplementation("io.github.openflocon:flocon:LAST_VERSION")
releaseImplementation("io.github.openflocon:flocon-no-op:LAST_VERSION")
```

in your `Application.kt`
```kotlin
// android initialization
Flocon.initialize(this)

// desktop / ios
Flocon.initialize()
```

Download & install the last `Desktop client` : https://github.com/openflocon/Flocon/releases

## 🤝 Contributors

Thanks to these amazing people for making Flocon better every day!

[See contributors list](docs/contributors.md)

## License

Flocon is MIT licensed, as found in the [LICENSE](/LICENSE) file.
