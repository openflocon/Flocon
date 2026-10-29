dependencyResolutionManagement {
    repositories {
        google()
        maven("https://maven-central.storage-download.googleapis.com/maven2/") // Google mirror of Maven Central, avoids rate limits
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"

include(":convention")
