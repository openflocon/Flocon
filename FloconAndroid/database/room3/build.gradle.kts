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

    wasmJs {
        moduleName = "flocon_database_room3"
        browser()
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":flocon"))
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.androidx.room3.runtime)
                implementation(libs.androidx.sqlite)
                implementation(project(":database:core"))
            }
        }

        val wasmJsMain by getting {
            dependsOn(commonMain)
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
    namespace = "io.github.openflocon.flocon.database.room3"
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
        artifactId = "flocon-database-room3",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
        name = "Flocon Room 3 Implementation"
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
