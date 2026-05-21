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
            }
        }
    }
}

android {
    namespace = "io.github.openflocon.flocon.sharedprefs.noop"
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-sharedprefs-no-op",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}
