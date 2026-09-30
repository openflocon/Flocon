plugins {
    id("flocon.kotlin.library")
    id("flocon.android.library")
    id("flocon.ios.library")
    id("flocon.jvm.library")
    id("flocon.wasm.library")
    id("flocon.publish")
}

kotlin {
    android {
        namespace = "io.github.openflocon.flocon.grpc.lite"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.flocon)
            implementation(projects.grpc.grpcInterceptorBase)
            implementation(libs.grpc.android)
            implementation(libs.gson)
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-grpc-interceptor-lite",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
        name = "Flocon Grpc Interceptor Lite"
    }
}
