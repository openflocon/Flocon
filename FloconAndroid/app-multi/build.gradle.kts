plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":flocon"))
                implementation(project(":flocon-base"))
                implementation(project(":ktor-interceptor"))

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                
                // Ktor client core
                implementation(libs.ktor.client.core)
                
                // Compose Multiplatform
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.lifecycle.runtime.ktx)
                implementation(libs.androidx.activity.compose)
                
                // Room for Android
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.room.ktx)
                
                // Ktor client for Android
                implementation(libs.ktor.client.okhttp)
            }
        }
        
        val desktopMain by getting {
            dependencies {
                // Ktor client for desktop/JVM
                implementation(libs.ktor.client.cio)
                
                // Compose Desktop
                implementation(compose.desktop.currentOs)
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
            dependencies {
                // Ktor client for iOS
                implementation(libs.ktor.client.cio)
            }
        }
    }
}

android {
    namespace = "io.github.openflocon.flocon.myapplication.multi"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.openflocon.flocon.myapplication.multi"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        named("debug")  {
            // just a dummy keystore to be able to test the release build
            keyAlias = "release"
            keyPassword = "release"
            storeFile = file("../app/release.jks")
            storePassword = "release"
        }
        register("release")  {
            keyAlias = "release"
            keyPassword = "release"
            storeFile = file("../app/release.jks")
            storePassword = "release"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    buildFeatures {
        compose = true
    }
    
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
}

compose.desktop {
    application {
        mainClass = "io.github.openflocon.flocon.myapplication.multi.MainKt"
        
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
            packageName = "FloconMultiApp"
            packageVersion = "1.0.0"
        }
    }
}

