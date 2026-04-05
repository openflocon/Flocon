import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("flocon.android.library")
    id("flocon.publish")
}

android {
    namespace = "io.github.openflocon.flocon.datastores"
}


dependencies {

    implementation(projects.flocon)

    implementation(platform(libs.kotlinx.coroutines.bom))
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.datastore.preferences)
}


mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-datastores-no-op",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}