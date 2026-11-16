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

// project path -> published artifactId. The no-op variant is "<path>-no-op" / "<artifactId>-no-op".
val floconModules = mapOf(
    ":flocon:flocon" to "flocon",
    ":analytics:analytics" to "flocon-analytics",
    ":accessibility:accessibility" to "flocon-accessibility",
    ":featureflags:featureflags" to "flocon-feature-flags",
    ":timeline:timeline" to "flocon-timeline",
    ":webview:webview" to "flocon-webview",
    ":crashreporter:crashreporter" to "flocon-crashreporter",
    ":database:core:core" to "flocon-database-core",
    ":database:room:room" to "flocon-database-room",
    ":database:room3:room3" to "flocon-database-room3",
    ":database:sqldelight:sqldelight" to "flocon-database-sqldelight",
    ":datastores:datastores" to "flocon-datastores",
    ":deeplinks:deeplinks" to "flocon-deeplinks",
    ":files:files" to "flocon-files",
    ":grpc:grpc-interceptor-base:grpc-interceptor-base" to "flocon-grpc-interceptor-base",
    ":grpc:grpc-interceptor:grpc-interceptor" to "flocon-grpc-interceptor",
    ":grpc:grpc-interceptor-lite:grpc-interceptor-lite" to "flocon-grpc-interceptor-lite",
    ":network:core:core" to "flocon-network-core",
    ":network:ktor-interceptor:ktor-interceptor" to "flocon-ktor-interceptor",
    ":network:okhttp-interceptor:okhttp-interceptor" to "flocon-okhttp-interceptor",
    ":sharedprefs:sharedprefs" to "flocon-sharedprefs",
    ":tables:tables" to "flocon-tables",
    ":qrscanner:qrscanner" to "flocon-qr-scanner",
)

// :sample:android release builds use the no-op version of every Flocon module,
// including the ones coming transitively from :sample-multiplatform
project(":sample:android").configurations.configureEach {
    if (name.startsWith("release")) {
        resolutionStrategy.dependencySubstitution {
            floconModules.forEach { (path, artifactId) ->
                val noOpModule = module("$floconGroupId:$artifactId-no-op:$floconMavenVersion")
                substitute(project(path)).using(if (floconUseMaven) noOpModule else project("$path-no-op"))
            }
        }
    }
}

if (floconUseMaven) {
    listOf(":sample:android", ":sample-multiplatform").forEach { samplePath ->
        project(samplePath).configurations.configureEach {
            if (samplePath == ":sample:android" && name.startsWith("release")) return@configureEach // no-op, see above
            resolutionStrategy.dependencySubstitution {
                floconModules.forEach { (path, artifactId) ->
                    substitute(project(path)).using(module("$floconGroupId:$artifactId:$floconMavenVersion"))
                }
            }
        }
    }
}
