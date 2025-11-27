🛠️ Getting Started

`This library is lightweight, contributing just 140KB to the overall app size`

in your module .kts

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon.svg)](https://search.maven.org/artifact/io.github.openflocon/flocon)
```
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

```toml
[versions]
flocon = "LASTEST_VERSION"

[libraries]
flocon = { module = "io.github.openflocon:flocon", version.ref = "flocon" }
```

Download & install the last `Desktop client`

https://github.com/openflocon/Flocon/releases