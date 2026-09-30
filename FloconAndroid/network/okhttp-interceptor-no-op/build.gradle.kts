plugins {
    id("flocon.kotlin.library")
    id("flocon.android.library")
    id("flocon.jvm.library")
    id("flocon.publish")
}

kotlin {
    android {
        namespace = "io.github.openflocon.flocon.okhttp"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.network.core)

            implementation(dependencies.platform(libs.okhttp.bom))
            implementation(libs.okhttp)
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-okhttp-interceptor-no-op",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}
