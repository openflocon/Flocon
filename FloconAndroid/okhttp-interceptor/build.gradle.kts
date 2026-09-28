plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.vanniktech.maven.publish)
}

configure<com.android.build.api.dsl.LibraryExtension> {
    namespace = "io.github.openflocon.flocon.okhttp"
}

dependencies {
    implementation(project(":flocon-base"))

    implementation(platform(libs.kotlinx.coroutines.bom))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.brotli.dec)

    testImplementation(libs.junit)
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-okhttp-interceptor",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
        name = "Flocon OkHttp Interceptor"
    }
}
