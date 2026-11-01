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

include(":sample-multiplatform")
include(":flocon:flocon")
include(":flocon:flocon-no-op")
include(":accessibility:accessibility")
include(":accessibility:accessibility-no-op")
include(":analytics:analytics")
include(":analytics:analytics-no-op")
include(":crashreporter:crashreporter")
include(":crashreporter:crashreporter-no-op")
include(":datastores:datastores")
include(":datastores:datastores-no-op")
include(":deeplinks:deeplinks")
include(":deeplinks:deeplinks-no-op")
include(":device:device")
include(":device:device-no-op")
include(":featureflags:featureflags")
include(":featureflags:featureflags-no-op")
include(":files:files")
include(":files:files-no-op")
include(":sharedprefs:sharedprefs")
include(":sharedprefs:sharedprefs-no-op")
include(":tables:tables")
include(":tables:tables-no-op")
include(":webview:webview")
include(":webview:webview-no-op")
include(":qrscanner:qrscanner")
include(":qrscanner:qrscanner-no-op")
include(":network:core:core")
include(":network:core:core-no-op")
include(":network:ktor-interceptor:ktor-interceptor")
include(":network:ktor-interceptor:ktor-interceptor-no-op")
include(":network:okhttp-interceptor:okhttp-interceptor")
include(":network:okhttp-interceptor:okhttp-interceptor-no-op")
include(":database:core:core")
include(":database:core:core-no-op")
include(":database:room:room")
include(":database:room:room-no-op")
include(":database:room3:room3")
include(":database:room3:room3-no-op")
include(":grpc:grpc-interceptor:grpc-interceptor")
include(":grpc:grpc-interceptor:grpc-interceptor-no-op")
include(":grpc:grpc-interceptor-base:grpc-interceptor-base")
include(":grpc:grpc-interceptor-base:grpc-interceptor-base-no-op")
include(":grpc:grpc-interceptor-lite:grpc-interceptor-lite")
include(":grpc:grpc-interceptor-lite:grpc-interceptor-lite-no-op")

includeBuild("build-logic")

include(":sample:android")
include(":sample:desktop")
include(":sample:wasm")
include(":sample:ios")
