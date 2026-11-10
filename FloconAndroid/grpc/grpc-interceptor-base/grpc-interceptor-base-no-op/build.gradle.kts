plugins {
    id("flocon.kotlin.library")
    id("flocon.android.library")
    id("flocon.publish")
}

kotlin {
    android {
        namespace = "io.github.openflocon.flocon.grpc.base.noop"
    }

    sourceSets {
        androidMain.dependencies {
            // FloconGrpcBaseInterceptor is a ClientInterceptor
            api(libs.grpc.android)
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-grpc-interceptor-base-no-op",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
        name = "Flocon gRPC Interceptor Base No-Op"
    }
}
