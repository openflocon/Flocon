import org.jetbrains.kotlin.gradle.internal.platform.wasm.WasmPlatforms.wasmJs

plugins {
    id("flocon.kotlin.multiplatform")
    id("flocon.publish")
}

kotlin {
//    androidTarget {
//        compilations.all {
//            kotlinOptions {
//                jvmTarget = "11"
//            }
//        }
//    }
//
//    jvm()
//
//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()
//
//    wasmJs {
//        moduleName = "flocon_datastores"
//        browser()
//        binaries.executable()
//    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.flocon)
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.datastore.preferences)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.androidx.datastore.preferences)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
//        val wasmJsMain by getting
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.androidx.datastore.preferences)
            }
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    namespace = "io.github.openflocon.flocon.datastores"
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-datastores",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}
