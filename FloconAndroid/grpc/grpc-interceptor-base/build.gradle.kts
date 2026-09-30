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
        namespace = "io.github.openflocon.flocon.grpc.base"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.flocon)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

dependencies {
    //implementation(projects.network.core)

//    implementation(platform(libs.kotlinx.coroutines.bom))
//    implementation(libs.kotlinx.coroutines.core)
//
//    implementation(libs.grpc.android)
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-grpc-interceptor-base",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
        name = "Flocon Grpc Interceptor"
    }
}
