plugins {
    id("flocon.kotlin.library")
    id("flocon.android.library")
    id("flocon.ios.library")
    id("flocon.jvm.library")
    id("flocon.wasm.library")
    id("flocon.publish")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "io.github.openflocon.flocon.network.core.noop"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.flocon)
            implementation(projects.network.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-network-core-no-op",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}

