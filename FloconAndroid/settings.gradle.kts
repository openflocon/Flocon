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
include(":flocon-base")
include(":flocon")
include(":flocon-no-op")
include(":okhttp-interceptor")
include(":grpc-interceptor")
