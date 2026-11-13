rootProject.name = "Flocon-Sample-App"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        maven("https://maven-central.storage-download.googleapis.com/maven2/") // Google mirror of Maven Central, avoids rate limits
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

dependencyResolutionManagement {
    // not FAIL_ON_PROJECT_REPOS: the Kotlin/Wasm tooling (binaryen, Node.js) adds its own download repositories
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        maven("https://maven-central.storage-download.googleapis.com/maven2/") // Google mirror of Maven Central, avoids rate limits
        mavenLocal()
    }
}

include(":bom")
include(":sample-multiplatform")
include(":flocon:flocon")
include(":flocon:flocon-no-op")
include(":accessibility:accessibility")
// the only no-ops left: their Android manifest entries can't be removed by flocon-no-op
include(":accessibility:accessibility-no-op")
include(":analytics:analytics")
include(":crashreporter:crashreporter")
include(":datastores:datastores")
include(":deeplinks:deeplinks")
include(":featureflags:featureflags")
include(":timeline:timeline")
include(":files:files")
include(":sharedprefs:sharedprefs")
include(":tables:tables")
include(":webview:webview")
include(":qrscanner:qrscanner")
include(":qrscanner:qrscanner-no-op")
include(":network:core:core")
include(":network:ktor-interceptor:ktor-interceptor")
include(":network:okhttp-interceptor:okhttp-interceptor")
include(":database:core:core")
include(":database:room:room")
include(":database:room3:room3")
include(":database:sqldelight:sqldelight")
include(":grpc:grpc-interceptor:grpc-interceptor")
include(":grpc:grpc-interceptor-base:grpc-interceptor-base")
include(":grpc:grpc-interceptor-lite:grpc-interceptor-lite")

includeBuild("build-logic")

include(":sample:android")
include(":sample:desktop")
include(":sample:wasm")
include(":sample:ios")
