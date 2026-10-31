plugins {
    id("flocon.kotlin.library")
    id("flocon.android.library")
    id("flocon.ios.library")
    id("flocon.jvm.library")
    // no wasm: DataStore does not support it
    id("flocon.publish")
}

kotlin {
    android {
        namespace = "io.github.openflocon.flocon.datastores"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.flocon.flocon)
            // FloconDatastorePreference implements FloconPreference
            api(projects.sharedprefs.sharedprefs)
            api(libs.androidx.datastore.preferences.core)

            implementation(dependencies.platform(libs.kotlinx.coroutines.bom))
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-datastores",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )


    pom {
        name = "Flocon Datastores"
    }
}
