plugins {
    id("flocon.android.library")
    id("flocon.publish")
}

android {
    namespace = "io.github.openflocon.flocon.okhttp"
}

dependencies {
    implementation(projects.network.coreNoOp)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-okhttp-interceptor-no-op",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}
