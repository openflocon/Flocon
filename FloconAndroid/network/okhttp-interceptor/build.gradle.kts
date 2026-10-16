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

            implementation(dependencies.platform(libs.kotlinx.coroutines.bom))
            implementation(libs.kotlinx.coroutines.core)

            implementation(dependencies.platform(libs.okhttp.bom))
            implementation(libs.okhttp)

            implementation(libs.brotli.dec)
        }

        androidMain.dependencies {
            implementation(dependencies.platform(libs.kotlinx.coroutines.bom))
            implementation(libs.kotlinx.coroutines.android)
        }

        jvmMain.dependencies {
            implementation(dependencies.platform(libs.okhttp.bom))
            implementation(libs.okhttp)
            implementation(dependencies.platform(libs.kotlinx.coroutines.bom))
            implementation(libs.kotlinx.coroutines.android)
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-okhttp-interceptor",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )


    pom {
        name = "Flocon OkHttp Interceptor"
    }
}
