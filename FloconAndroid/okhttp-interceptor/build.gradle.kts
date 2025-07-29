plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.github.openflocon.flocon.okhttp"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    publishing {
        // Tell Android to publish the release variant
        singleVariant("release")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(project(":core"))

    // not sure
    implementation(libs.androidx.core.ktx)

    implementation(platform(libs.kotlinx.coroutines.bom))
    implementation(libs.jetbrains.kotlinx.coroutines.core)
    implementation(libs.jetbrains.kotlinx.coroutines.android)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp3.okhttp)
}


publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = project.property("floconGroupId") as String
            artifactId = "flocon-okhttp"
            version = project.property("floconVersion") as String
            // Wait for Android to finish configuration
            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        mavenLocal()
    }
}