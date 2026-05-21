plugins {
    id("flocon.kotlin.multiplatform")
    id("flocon.publish")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":flocon"))
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}

android {
    namespace = "io.github.openflocon.flocon.device"
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-device",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}
