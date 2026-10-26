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

            // OkHttp is provided by the app (4.12+ or 5.x): Flocon does not impose any version.
            compileOnly(libs.okhttp4)

            implementation(libs.brotli.dec)
        }

        androidMain.dependencies {
            implementation(dependencies.platform(libs.kotlinx.coroutines.bom))
            implementation(libs.kotlinx.coroutines.android)
        }

        jvmTest.dependencies {
            // compileOnly is not on the test classpath
            implementation(libs.okhttp4)
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
