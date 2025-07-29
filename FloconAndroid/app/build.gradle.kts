import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.protobuf")
}

android {
    namespace = "com.github.openflocon.flocon.myapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.github.openflocon.flocon.myapplication"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":okhttp-interceptor"))
    implementation(project(":grpc-interceptor"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(platform(libs.kotlinx.coroutines.bom))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // region okhttp
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    // endregion

    // region grpc
    implementation(libs.grpc.android)
    implementation(libs.grpc.kotin.stub)
    implementation(libs.grpc.protobuf.lite)
    implementation(libs.grpc.okhttp)
    implementation(libs.protobuf.kotlin.lite)
    // endregion

    // region coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    // endregion

    // region room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    // endregion
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }

    generateProtoTasks {
        val protocGenJava = "io.grpc:protoc-gen-grpc-java:1.73.0"
        val protocGenKotlin = "io.grpc:protoc-gen-grpc-kotlin:1.4.3" + ":jdk8@jar"

        plugins {
            id("java") {
                artifact = protocGenJava
            }
            id("grpc") {
                artifact = protocGenJava
            }
            id("grpckt") {
                artifact = protocGenKotlin
            }
        }

        all().forEach {
            it.plugins {
                id("java") {
                    option("lite")
                }
                id("grpc") {
                    option("lite")
                }
                id("grpckt") {
                    option("lite")
                }
            }
            it.builtins {
                id("kotlin") {
                    option("lite")
                }
            }
        }
    }
}