plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.vanniktech.maven.publish)
}

configure<com.android.build.api.dsl.LibraryExtension> {
    namespace = "io.github.openflocon.flocon.okhttp"
}

dependencies {
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-analytics-no-op",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
        name = "Flocon OkHttp Interceptor"
    }
}
