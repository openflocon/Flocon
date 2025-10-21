pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "My Application"
include(":app")
include(":app-multi")
include(":flocon-base")
include(":flocon")
include(":flocon-no-op")
include(":okhttp-interceptor")
include(":okhttp-interceptor-no-op")
include(":grpc:grpc-interceptor")
include(":grpc:grpc-interceptor-base")
include(":grpc:grpc-interceptor-lite")
include(":ktor-interceptor")
