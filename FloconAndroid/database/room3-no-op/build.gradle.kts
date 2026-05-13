import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.vanniktech.maven.publish)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
<<<<<<<< HEAD:FloconAndroid/database/core-no-op/build.gradle.kts
                implementation(project(":flocon"))
                implementation(libs.jetbrains.kotlinx.coroutines.core.fixed)
========
                implementation(project(":network:core-no-op"))
                implementation(libs.ktor.client.core)
                implementation(project(":database:core-no-op"))
>>>>>>>> 2.0.0:FloconAndroid/database/room3-no-op/build.gradle.kts
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
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
<<<<<<<< HEAD:FloconAndroid/database/core-no-op/build.gradle.kts
    namespace = "io.github.openflocon.flocon.database.core.noop"
========
    namespace = "io.github.openflocon.flocon.database.room3.noop"
>>>>>>>> 2.0.0:FloconAndroid/database/room3-no-op/build.gradle.kts
    compileSdk = 36

    defaultConfig {
        minSdk = 23
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)

    if (project.hasProperty("signing.required") && project.property("signing.required") == "false") {
        // Skip signing
    } else {
        signAllPublications()
    }

    coordinates(
        groupId = project.property("floconGroupId") as String,
<<<<<<<< HEAD:FloconAndroid/database/core-no-op/build.gradle.kts
        artifactId = "flocon-database-core-no-op",
========
        artifactId = "flocon-database-room3-no-op",
>>>>>>>> 2.0.0:FloconAndroid/database/room3-no-op/build.gradle.kts
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
<<<<<<<< HEAD:FloconAndroid/database/core-no-op/build.gradle.kts
        name = "Flocon Database Core No-Op"
========
        name = "Flocon Room 3 Implementation No-Op"
>>>>>>>> 2.0.0:FloconAndroid/database/room3-no-op/build.gradle.kts
        description = project.property("floconDescription") as String
        inceptionYear = "2025"
        url = "https://github.com/openflocon/Flocon"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "openflocon"
                name = "Open Flocon"
                url = "https://github.com/openflocon"
            }
        }
        scm {
            url = "https://github.com/openflocon/Flocon"
            connection = "scm:git:git://github.com/openflocon/Flocon.git"
            developerConnection = "scm:git:ssh://git@github.com/openflocon/Flocon.git"
        }
    }
}
