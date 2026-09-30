import com.google.protobuf.gradle.id
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.apollo)
    alias(libs.plugins.protobuf)
}

fun com.android.build.api.dsl.AndroidSourceSet.proto(action: org.gradle.api.file.SourceDirectorySet.() -> Unit) {
    (this as? org.gradle.api.plugins.ExtensionAware)?.extensions?.getByName("proto")?.let {
        it as? org.gradle.api.file.SourceDirectorySet
    }?.apply(action)
}

kotlin {
    android {
        namespace = "io.github.openflocon.flocon.myapplication.multi"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        androidResources {
            enable = true
        }
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop")

    // Compose Multiplatform 1.11 dropped the iosX64 (Intel Mac simulator) variant, so the
    // sample's iosX64CompilationDependenciesMetadata can no longer resolve compose.runtime
    // for that target. Apple-silicon devs use iosSimulatorArm64; real devices use iosArm64.
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.flocon)
                implementation(projects.deeplinks)
                implementation(projects.network.ktorInterceptor)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)

                // Ktor client core
                implementation(libs.ktor.client.core)

                // room
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.sqlite.bundled)

                // Compose Multiplatform
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)

                implementation(libs.coil.compose)
                implementation(libs.coil.network.ktor)

                // Apollo (GraphQL)
                implementation(libs.apollo.runtime)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.lifecycle.runtime.ktx)
                implementation(libs.androidx.activity.compose)

                // Ktor client for Android
                implementation(libs.ktor.client.okhttp)

                // OkHttp
                implementation(libs.okhttp)

                // gRPC
                implementation(libs.grpc.android)
                implementation(libs.grpc.kotlin.stub)
                implementation(libs.grpc.protobuf.lite)
                implementation(libs.grpc.okhttp)
                implementation(libs.protobuf.kotlin.lite)

                // Coil with OkHttp network fetcher
                implementation(libs.coil.network.okhttp)

                // Datastore
                implementation(libs.androidx.datastore.preferences)
            }
        }

        val desktopMain by getting {
            dependencies {
                // Ktor client for desktop/JVM
                implementation(libs.ktor.client.cio)

                implementation(libs.sqlite.jdbc)
                implementation(libs.androidx.sqlite.bundled)

                // Compose Desktop
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.ktor.client.java)
            }
        }

        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                // Ktor client for iOS
                implementation(libs.ktor.client.cio)
            }
        }
    }
}


dependencies {
    listOf(
        "kspAndroid",
        "kspDesktop",
        "kspIosSimulatorArm64",
        "kspIosArm64"
    ).forEach {
        add(it, libs.androidx.room.compiler)
    }

    // Flocon Android specific plugins (variant-aware dependencies)
    add("debugImplementation", projects.network.okhttpInterceptor)
    add("releaseImplementation", projects.network.okhttpInterceptorNoOp)

    add("debugImplementation", projects.sharedprefs)
    add("releaseImplementation", projects.sharedprefsNoOp)

    add("debugImplementation", projects.datastores)
    add("releaseImplementation", projects.datastoresNoOp)

    add("debugImplementation", projects.grpc.grpcInterceptorLite)

    add("debugImplementation", projects.analytics)
    add("releaseImplementation", projects.analyticsNoOp)

    add("debugImplementation", projects.crashreporter)
    add("releaseImplementation", projects.crashreporterNoOp)

    add("debugImplementation", projects.tables)
    add("releaseImplementation", projects.tablesNoOp)

    add("debugImplementation", project(":database:room"))
    add("releaseImplementation", project(":database:room-no-op"))
    add("debugImplementation", project(":database:room3"))
    add("releaseImplementation", project(":database:room3-no-op"))
}

room {
    schemaDirectory("$projectDir/schemas")
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

apollo {
    service("github") {
        packageName.set("com.github")
        srcDir("src/androidMain/graphql")
    }
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }

    generateProtoTasks {
        val protocGenJava = libs.grpc.gen.java.get().toString()
        val protocGenKotlin = libs.grpc.gen.kotlin.get().toString() + ":jdk8@jar"

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

