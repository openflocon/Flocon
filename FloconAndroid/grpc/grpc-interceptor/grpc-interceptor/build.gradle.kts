plugins {
    id("flocon.kotlin.library")
    id("flocon.android.library")
    id("flocon.publish")
}

kotlin {
    android {
        namespace = "io.github.openflocon.flocon.grpc"
    }

    sourceSets {
        androidMain.dependencies {
            implementation(projects.flocon.flocon)
            api(projects.grpc.grpcInterceptorBase.grpcInterceptorBase)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.grpc.android)
            implementation(libs.protobuf.util)
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-grpc-interceptor",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )


    pom {
        name = "Flocon gRPC Interceptor"
    }
}
