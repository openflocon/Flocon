plugins {
    id("flocon.kotlin.library")
    id("flocon.android.library")
    id("flocon.ios.library")
    id("flocon.jvm.library")
    id("flocon.wasm.library")
    id("flocon.publish")
}

// Turns Flocon off: add it next to the real Flocon modules in release builds.
// It brings the real API, plus what turns it off: a marker class found at runtime,
// and an R8 rule that removes the Flocon code from minified Android builds.
kotlin {
    android {
        namespace = "io.github.openflocon.flocon.noop"

        optimization {
            consumerKeepRules.publish = true
            consumerKeepRules.file("consumer-rules.pro")
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.flocon.flocon)
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-no-op",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
        name = "Flocon No-Op"
        description = "Turns Flocon off in the builds it is added to, such as release builds."
    }
}
