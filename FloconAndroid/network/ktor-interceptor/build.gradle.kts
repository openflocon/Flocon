plugins {
    id("flocon.kotlin.multiplatform")
    id("flocon.publish")
}

kotlin {
    wasmJs {
        outputModuleName = "flocon_ktor_interceptor"
        browser()
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {

                api(projects.flocon)
                api(projects.network.core)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.core)
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation(libs.brotli.dec)
            }
        }
        
        val jvmMain by getting {
            dependencies {
                implementation(libs.brotli.dec)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val wasmJsMain by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    namespace = "io.github.openflocon.flocon.ktor"
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-ktor-interceptor",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}
