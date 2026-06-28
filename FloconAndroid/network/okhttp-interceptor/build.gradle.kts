plugins {
    id("flocon.android.library")
    id("flocon.publish")
}

android {
    namespace = "io.github.openflocon.flocon.okhttp"
}

dependencies {
    api(projects.network.core)

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
}
