plugins {
    id("flocon.kotlin.library")
    id("flocon.android.library")
    id("flocon.ios.library")
    id("flocon.jvm.library")
    id("flocon.wasm.library")
    id("flocon.publish")
    alias(libs.plugins.kotlin.serialization) // TODO Check if needed
}

kotlin {
    android {
        namespace = "io.github.openflocon.flocon.database.core"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.flocon)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }

        iosMain.dependencies {
            implementation(libs.androidx.sqlite.bundled) // TODO Check if needed
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-database-core",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}

