plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false

    alias(libs.plugins.compose.multiplatform) apply false

    alias(libs.plugins.ksp) apply false

    alias(libs.plugins.vanniktech.maven.publish) apply false

    alias(libs.plugins.protobuf) apply false

    alias(libs.plugins.buildconfig) apply false

    alias(libs.plugins.sqldelight) apply false
}

// Samples consume either the local Flocon modules (default) or the artifacts published on Maven,
// to check that a release was uploaded correctly:
//   ./gradlew :sample:android:assembleDebug -PfloconUseMaven=true [-PfloconMavenVersion=x.y.z]
val floconUseMaven = providers.gradleProperty("floconUseMaven").map(String::toBoolean).getOrElse(false)
val floconMavenVersion = providers.gradleProperty("floconMavenVersion").orElse(providers.gradleProperty("floconVersion")).get()
val floconGroupId = providers.gradleProperty("floconGroupId").get()

// project path -> published artifactId
val floconModules = mapOf(
    ":flocon:flocon" to "flocon",
    ":flocon:flocon-no-op" to "flocon-no-op",
    ":accessibility:accessibility" to "flocon-accessibility",
    ":database:room:room" to "flocon-database-room",
    ":database:room3:room3" to "flocon-database-room3",
    ":database:sqldelight:sqldelight" to "flocon-database-sqldelight",
    ":datastores:datastores" to "flocon-datastores",
    ":grpc:grpc-interceptor-base:grpc-interceptor-base" to "flocon-grpc-interceptor-base",
    ":grpc:grpc-interceptor:grpc-interceptor" to "flocon-grpc-interceptor",
    ":grpc:grpc-interceptor-lite:grpc-interceptor-lite" to "flocon-grpc-interceptor-lite",
    ":network:ktor-interceptor:ktor-interceptor" to "flocon-ktor-interceptor",
    ":webview:webview" to "flocon-webview",
    ":qrscanner:qrscanner" to "flocon-qr-scanner",
)

// used by the samples to declare their dependencies: see flocon() in sample/android/build.gradle.kts
extra["floconUseMaven"] = floconUseMaven
extra["floconMavenCoordinates"] = floconModules.mapValues { (_, artifactId) -> "$floconGroupId:$artifactId:$floconMavenVersion" }

// :sample:android release builds add flocon-no-op, which turns Flocon off. The only modules it can't handle
// are the ones with Android manifest entries: they are swapped for their "-no-op" sibling, including when
// they come transitively from :sample-multiplatform
val floconManifestModules = mapOf(
    ":accessibility:accessibility" to "flocon-accessibility",
    ":qrscanner:qrscanner" to "flocon-qr-scanner",
)
project(":sample:android").configurations.configureEach {
    if (name.startsWith("release")) {
        resolutionStrategy.dependencySubstitution {
            floconManifestModules.forEach { (path, artifactId) ->
                val noOpModule = module("$floconGroupId:$artifactId-no-op:$floconMavenVersion")
                substitute(project(path)).using(if (floconUseMaven) noOpModule else project("$path-no-op"))
            }
        }
    }
}

if (floconUseMaven) {
    listOf(":sample:android", ":sample-multiplatform").forEach { samplePath ->
        project(samplePath).configurations.configureEach {
            if (samplePath == ":sample:android" && name.startsWith("release")) return@configureEach // manifest no-ops, see above
            resolutionStrategy.dependencySubstitution {
                floconModules.forEach { (path, artifactId) ->
                    substitute(project(path)).using(module("$floconGroupId:$artifactId:$floconMavenVersion"))
                }
            }
        }
    }
}
