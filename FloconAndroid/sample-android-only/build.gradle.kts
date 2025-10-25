import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.apollo)
    id("com.google.protobuf")
}

android {
    namespace = "io.github.openflocon.flocon.myapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.openflocon.flocon.myapplication"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val githubToken = System.getenv("GITHUB_TOKEN_GRPC") ?: ""

    signingConfigs {
        named("debug")  {
            // just a dummy keystore to be able to test the release build
            keyAlias = "release"
            keyPassword = "release"
            storeFile = file("release.jks")
            storePassword = "release"
        }
        register("release")  {
            keyAlias = "release"
            keyPassword = "release"
            storeFile = file("release.jks")
            storePassword = "release"
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "GITHUB_TOKEN", "\"$githubToken\"")
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            buildConfigField("String", "GITHUB_TOKEN", "\"$githubToken\"")
            signingConfig = signingConfigs.getByName("release")
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
        buildConfig = true
    }
}

val useMaven = false
dependencies {
    if(useMaven) {
        val floconVersion = "1.4.0"
        implementation("io.github.openflocon:flocon:$floconVersion")
        //implementation("io.github.openflocon:flocon-no-op:$floconVersion")
        implementation("io.github.openflocon:flocon-grpc-interceptor-lite:$floconVersion")
        implementation("io.github.openflocon:flocon-okhttp-interceptor:$floconVersion")
        //implementation("io.github.openflocon:flocon-okhttp-interceptor-no-op:$floconVersion")
        implementation("io.github.openflocon:flocon-ktor-interceptor:$floconVersion")
    } else {
        debugImplementation(project(":flocon"))
        releaseImplementation(project(":flocon-no-op"))
        debugImplementation(project(":okhttp-interceptor"))
        releaseImplementation(project(":okhttp-interceptor-no-op"))
        implementation(project(":grpc:grpc-interceptor-lite"))
        debugImplementation(project(":ktor-interceptor"))
        releaseImplementation(project(":ktor-interceptor-no-op"))
    }


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
    implementation(libs.grpc.kotlin.stub)
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
    // endregion

    // region graphql
    implementation(libs.apollo.runtime)
    //implementation(libs.apollo.http.okhttprealization)
    // endregion

    // region ktor
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.core)
    //endregion
}

apollo {
    service("github") {
        packageName.set("com.github")
    }
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