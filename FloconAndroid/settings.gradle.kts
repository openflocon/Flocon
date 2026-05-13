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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

include(":sample-android-only")
include(":sample-multiplatform")
include(":flocon")
include(":flocon-no-op")
include(":network:okhttp-interceptor")
include(":network:okhttp-interceptor-no-op")
include(":grpc:grpc-interceptor")
include(":grpc:grpc-interceptor-base")
include(":grpc:grpc-interceptor-lite")
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
include(":network:core")
include(":network:core-no-op")
include(":database:core")
include(":database:core-no-op")
include(":database:room")
include(":database:room-no-op")
include(":database:room3")
include(":database:room3-no-op")

includeBuild("build-logic")

