plugins {
    id("flocon.android.library")
    id("flocon.publish")
}

android {
    namespace = "io.github.openflocon.flocon.grpc"
}


dependencies {
    api(project(":grpc:grpc-interceptor-base"))

    implementation(libs.grpc.android)
    implementation(libs.protobuf.util)
}


mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-grpc-interceptor",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}