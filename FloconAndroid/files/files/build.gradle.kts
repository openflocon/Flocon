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
        namespace = "io.github.openflocon.flocon.files"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.flocon.flocon)
            implementation(dependencies.platform(libs.kotlinx.coroutines.bom))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }

        // java.io.File code shared by Android and the JVM
        val jvmAndroidMain by creating {
            dependsOn(commonMain.get())
        }
        androidMain.get().dependsOn(jvmAndroidMain)
        jvmMain.get().dependsOn(jvmAndroidMain)
    }
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-files",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )


    pom {
        name = "Flocon Files"
    }
}
