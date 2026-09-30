import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.maven.publish)
}

kotlin {
    android {
        namespace = "io.github.openflocon.flocon.ktor"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    wasmJs {
        outputModuleName = "flocon_database_room3_no_op"
        browser()
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":network:core-no-op"))
                implementation(libs.ktor.client.core)
                implementation(project(":database:core-no-op"))
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



mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-database-room3-no-op",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
        name = "Flocon Ktor Interceptor No Op"
    }
}