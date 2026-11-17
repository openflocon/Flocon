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

// Flocon modules: local project, or the published Maven artifact with -PfloconUseMaven=true
// (the root build.gradle.kts also substitutes the transitive ones coming from :sample-multiplatform)
@Suppress("UNCHECKED_CAST")
val floconCoordinates = rootProject.extra["floconMavenCoordinates"] as Map<String, String>
val floconUseMaven = rootProject.extra["floconUseMaven"] as Boolean
fun flocon(module: ProjectDependency): Any =
    if (floconUseMaven) floconCoordinates.getValue(module.path) else module

dependencies {
    implementation(projects.sampleMultiplatform)

    implementation(flocon(projects.flocon.flocon))
    implementation(flocon(projects.deeplinks.deeplinks))
    implementation(flocon(projects.database.room3.room3))
    implementation(flocon(projects.database.room.room))
    implementation(flocon(projects.database.sqldelight.sqldelight))
    implementation(flocon(projects.network.ktorInterceptor.ktorInterceptor))
    implementation(flocon(projects.network.okhttpInterceptor.okhttpInterceptor))
    implementation(flocon(projects.sharedprefs.sharedprefs))
    implementation(flocon(projects.files.files))
    implementation(flocon(projects.datastores.datastores))
    implementation(flocon(projects.analytics.analytics))
    implementation(flocon(projects.accessibility.accessibility))
    implementation(flocon(projects.featureflags.featureflags))
    implementation(flocon(projects.timeline.timeline))
    implementation(flocon(projects.webview.webview))
    implementation(flocon(projects.crashreporter.crashreporter))
    implementation(flocon(projects.tables.tables))
    implementation(flocon(projects.qrscanner.qrscanner))

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)

    // gRPC
    implementation(flocon(projects.grpc.grpcInterceptorLite.grpcInterceptorLite))
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
