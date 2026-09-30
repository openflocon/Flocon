plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.vanniktech.maven.publish)
}

configure<com.android.build.api.dsl.LibraryExtension> {
    namespace = "io.github.openflocon.flocon.grpc.base"
}

dependencies {
    implementation(projects.network.core)

    implementation(platform(libs.kotlinx.coroutines.bom))
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.grpc.android)
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-grpc-interceptor-base",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
        name = "Flocon Grpc Interceptor"
    }
}
