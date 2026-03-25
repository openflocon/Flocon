import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("flocon.kotlin.multiplatform")
    id("flocon.publish")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.flocon)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        
        val androidMain by getting {
            dependencies {
            }
        }
        
        val jvmMain by getting {
            dependencies {
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
    namespace = "io.github.openflocon.flocon"
}


mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-no-op",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}
