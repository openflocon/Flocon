plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.flocon.sample.android"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.flocon.sample.android"
        minSdk = 24
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.firebase.crashlytics.buildtools)

    implementation(projects.sampleMultiplatform)

    implementation(projects.flocon)
    implementation(projects.deeplinks)
    implementation(projects.database.room3)
    implementation(projects.database.room)
    implementation(projects.network.ktorInterceptor)
    implementation(projects.network.okhttpInterceptor)
    implementation(projects.sharedprefs)
    implementation(projects.files)
    implementation(projects.device)
    implementation(projects.datastores)
    implementation(projects.analytics)
    implementation(projects.crashreporter)
    implementation(projects.tables)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)

    implementation(libs.ktor.client.okhttp)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
}
// Release builds use the no-op version of every Flocon module,
// including the ones coming transitively from :sample-multiplatform
val floconNoOpModules = mapOf(
    ":flocon" to ":flocon-no-op",
    ":analytics" to ":analytics-no-op",
    ":crashreporter" to ":crashreporter-no-op",
    ":database:core" to ":database:core-no-op",
    ":database:room" to ":database:room-no-op",
    ":database:room3" to ":database:room3-no-op",
    ":datastores" to ":datastores-no-op",
    ":deeplinks" to ":deeplinks-no-op",
    ":device" to ":device-no-op",
    ":files" to ":files-no-op",
    ":network:core" to ":network:core-no-op",
    ":network:ktor-interceptor" to ":network:ktor-interceptor-no-op",
    ":network:okhttp-interceptor" to ":network:okhttp-interceptor-no-op",
    ":sharedprefs" to ":sharedprefs-no-op",
    ":tables" to ":tables-no-op",
)

configurations.configureEach {
    if (name.startsWith("release")) {
        resolutionStrategy.dependencySubstitution {
            floconNoOpModules.forEach { (module, noOp) ->
                substitute(project(module)).using(project(noOp))
            }
        }
    }
}
