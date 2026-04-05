import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("flocon.android.library")
    id("flocon.publish")
}


android {
    namespace = "io.github.openflocon.flocon.grpc.lite"
}


dependencies {
    api(projects.grpc.grpcInterceptorBase)

    implementation(libs.grpc.android)
    implementation(libs.gson)
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-grpc-interceptor-lite",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}