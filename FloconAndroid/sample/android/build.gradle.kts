import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.protobuf)
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

        // optional, used by the GraphQL test (the calls are rejected without it, but still visible in Flocon)
        buildConfigField("String", "GITHUB_TOKEN", "\"${System.getenv("GITHUB_TOKEN") ?: ""}\"")
    }

    buildTypes {
        release {
            // R8 on: flocon-no-op removes the Flocon code from the release build (checked by the CI)
            optimization {
                enable = true
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(projects.sampleMultiplatform)

    implementation(projects.flocon.flocon)
    // turns Flocon off in release builds
    releaseImplementation(projects.flocon.floconNoOp)
    implementation(projects.database.room3.room3)
    implementation(projects.database.room.room)
    implementation(projects.database.sqldelight.sqldelight)
    implementation(projects.network.ktorInterceptor.ktorInterceptor)
    implementation(projects.network.okhttpInterceptor.okhttpInterceptor)
    implementation(projects.datastores.datastores)
    implementation(projects.accessibility.accessibility)
    implementation(projects.webview.webview)
    implementation(projects.qrscanner.qrscanner)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)

    // gRPC
    implementation(projects.grpc.grpcInterceptorLite.grpcInterceptorLite)
    implementation(libs.grpc.android)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.kotlin.stub)
    implementation(libs.grpc.protobuf.lite)
    implementation(libs.grpc.okhttp)
    implementation(libs.protobuf.kotlin.lite)

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
protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }

    generateProtoTasks {
        val protocGenJava = libs.grpc.protoc.gen.java.get().toString()
        val protocGenKotlin = libs.grpc.protoc.gen.kotlin.get().toString() + ":jdk8@jar"

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

// accessibility and qrscanner add Android manifest entries (a permission, ML Kit) that flocon-no-op
// can't remove: release builds use their no-op, including through :sample-multiplatform
configurations.configureEach {
    if (name.startsWith("release")) {
        resolutionStrategy.dependencySubstitution {
            substitute(project(":accessibility:accessibility")).using(project(":accessibility:accessibility-no-op"))
            substitute(project(":qrscanner:qrscanner")).using(project(":qrscanner:qrscanner-no-op"))
        }
    }
}
