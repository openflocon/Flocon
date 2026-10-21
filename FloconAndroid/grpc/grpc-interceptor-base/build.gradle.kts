plugins {
    id("flocon.kotlin.library")
    id("flocon.android.library")
    id("flocon.publish")
}

kotlin {
    android {
        namespace = "io.github.openflocon.flocon.grpc.base"
    }

    sourceSets {
        androidMain.dependencies {
            api(projects.flocon)
            api(projects.network.core)
            // FloconGrpcBaseInterceptor is a ClientInterceptor
            api(libs.grpc.android)

            implementation(dependencies.platform(libs.kotlinx.coroutines.bom))
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-grpc-interceptor-base",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
        name = "Flocon gRPC Interceptor Base"
    }
}
