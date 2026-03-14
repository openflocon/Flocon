plugins {
    id("flocon.android.library")
    id("flocon.publish")
}


android {
    namespace = "io.github.openflocon.flocon.okhttp"
}


dependencies {
    implementation(project(":network:core-no-op"))
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp3.okhttp)
}


mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-okhttp-interceptor-no-op",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}