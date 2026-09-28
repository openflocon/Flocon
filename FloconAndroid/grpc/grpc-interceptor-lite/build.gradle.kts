plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.vanniktech.maven.publish)
}

configure<com.android.build.api.dsl.LibraryExtension> {
    namespace = "io.github.openflocon.flocon.grpc.lite"
}

dependencies {
    api(project(":grpc:grpc-interceptor-base"))

    implementation(libs.grpc.android)
    implementation(libs.gson)
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-grpc-interceptor-lite",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
        name = "Flocon Grpc Interceptor Lite"
    }
}
