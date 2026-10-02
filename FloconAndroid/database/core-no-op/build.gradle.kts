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
        namespace = "io.github.openflocon.flocon.database.core.noop"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.flocon)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-database-core-no-op",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )


    pom {
        name = "Flocon Database Core No-Op"
    }
}

