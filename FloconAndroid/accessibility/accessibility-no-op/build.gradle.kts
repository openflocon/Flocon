plugins {
    id("flocon.kotlin.library")
    id("flocon.android.library")
    id("flocon.jvm.library")
    id("flocon.ios.library")
    id("flocon.wasm.library")
    id("flocon.publish")
}

kotlin {
    android {
        namespace = "io.github.openflocon.flocon.accessibility.noop"
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.flocon.floconNoOp)
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-accessibility-no-op",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
        name = "Flocon Accessibility No-Op"
    }
}
