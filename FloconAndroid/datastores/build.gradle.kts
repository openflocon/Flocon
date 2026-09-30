plugins {
    id("flocon.kotlin.library")
    id("flocon.android.library")
    id("flocon.ios.library")
    id("flocon.jvm.library")
    // id("flocon.wasm.library") TODO add it 1.3.0
    id("flocon.publish")
}

kotlin {
    android {
        namespace = "io.github.openflocon.flocon.datastores"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.flocon)
            implementation(libs.androidx.datastore.core)
        }

        androidMain.dependencies {
            implementation(libs.androidx.datastore.preferences)
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-datastores",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}
