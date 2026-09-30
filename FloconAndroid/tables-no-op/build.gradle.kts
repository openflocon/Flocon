plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.maven.publish)
}

kotlin {
    android {
        namespace = "io.github.openflocon.flocon.base"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation(dependencies.platform(libs.kotlinx.coroutines.bom))
                implementation(libs.kotlinx.coroutines.android)
            }
        }
        
        val jvmMain by getting {
            dependencies {
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


mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-tables-no-op",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )


    pom {
        name = "Flocon"
    }
}
