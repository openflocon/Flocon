plugins {
    id("flocon.android.library")
    id("flocon.publish")
}


android {
    namespace = "io.github.openflocon.flocon.grpc.base"
}


dependencies {
    implementation(projects.flocon)

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
}