plugins {
    id("flocon.kotlin.library")
    id("flocon.android.library")
    id("flocon.ios.library")
    id("flocon.jvm.library")
    id("flocon.wasm.library")
    id("flocon.publish")
}

kotlin {
    android {
        namespace = "io.github.openflocon.flocon.connect"
        // the network security config, allowing the connection to any IP address, is an Android resource
        androidResources { enable = true }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.flocon)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-connect",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
        name = "Flocon Connect"
    }
}
