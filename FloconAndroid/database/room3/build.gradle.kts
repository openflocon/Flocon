import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.vanniktech.maven.publish)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    jvm()

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":flocon"))
<<<<<<<< HEAD:FloconAndroid/database/core-no-op/build.gradle.kts
                implementation(libs.jetbrains.kotlinx.coroutines.core.fixed)
========
                implementation(project(":database:core"))
                implementation(libs.androidx.room3.runtime)
                implementation(libs.androidx.sqlite.bundled)
>>>>>>>> 2.0.0:FloconAndroid/database/room3/build.gradle.kts
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

        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // KSP for Room
    add("kspCommonMainMetadata", libs.androidx.room3.compiler)
    add("kspAndroid", libs.androidx.room3.compiler)
    add("kspJvm", libs.androidx.room3.compiler)
    add("kspIosArm64", libs.androidx.room3.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room3.compiler)
}

android {
<<<<<<<< HEAD:FloconAndroid/database/core-no-op/build.gradle.kts
    namespace = "io.github.openflocon.flocon.database.core.noop"
========
    namespace = "io.github.openflocon.flocon.database.room3"
>>>>>>>> 2.0.0:FloconAndroid/database/room3/build.gradle.kts
    compileSdk = 36

    defaultConfig {
        minSdk = 23
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
        artifactId = "flocon-database-room3",
>>>>>>>> 2.0.0:FloconAndroid/database/room3/build.gradle.kts
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
<<<<<<<< HEAD:FloconAndroid/database/core-no-op/build.gradle.kts
        name = "Flocon Database Core No-Op"
========
        name = "Flocon Room 3 Implementation"
>>>>>>>> 2.0.0:FloconAndroid/database/room3/build.gradle.kts
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
