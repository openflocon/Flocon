rootProject.name = "Flocon-Sample-App"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
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
        mavenCentral()
        mavenLocal()
    }
}

include(":sample-multiplatform")
include(":flocon")
include(":flocon-no-op")
include(":connect")
include(":connect-no-op")
include(":network:okhttp-interceptor")
include(":network:okhttp-interceptor-no-op")
include(":grpc:grpc-interceptor")
include(":grpc:grpc-interceptor-base")
include(":grpc:grpc-interceptor-lite")
include(":grpc:grpc-interceptor-base-no-op")
include(":grpc:grpc-interceptor-no-op")
include(":grpc:grpc-interceptor-lite-no-op")
include(":network:ktor-interceptor")
include(":network:ktor-interceptor-no-op")
include(":datastores")
include(":datastores-no-op")
include(":deeplinks")
include(":deeplinks-no-op")
include(":tables")
include(":tables-no-op")
include(":analytics")
include(":analytics-no-op")
include(":featureflags")
include(":featureflags-no-op")
include(":crashreporter")
include(":crashreporter-no-op")
include(":device")
include(":device-no-op")
include(":files")
include(":files-no-op")
include(":sharedprefs")
include(":sharedprefs-no-op")
include(":network:core")
include(":network:core-no-op")
include(":database:core")
include(":database:core-no-op")
include(":database:room")
include(":database:room-no-op")
include(":database:room3")
include(":database:room3-no-op")

includeBuild("build-logic")

include(":sample:android")
include(":sample:desktop")
include(":sample:wasm")
include(":sample:ios")
