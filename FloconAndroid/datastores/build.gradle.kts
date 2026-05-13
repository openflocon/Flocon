import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("flocon.android.library")
    id("flocon.publish")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    wasmJs {
        moduleName = "flocon_datastores"
        browser()
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":flocon"))
                implementation(libs.jetbrains.kotlinx.coroutines.core.fixed)
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
        val wasmJsMain by getting
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


    implementation(projects.flocon)

    implementation(platform(libs.kotlinx.coroutines.bom))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.datastore.preferences)
}


mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-datastores",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}