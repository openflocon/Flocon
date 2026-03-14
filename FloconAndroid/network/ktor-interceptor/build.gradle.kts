plugins {
    id("flocon.kotlin.multiplatform")
    id("flocon.publish")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {

                api(project(":flocon"))
                api(project(":network:core"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
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
