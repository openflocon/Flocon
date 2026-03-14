plugins {
    id("flocon.android.library")
    id("flocon.publish")
}

dependencies {

    implementation(project(":flocon"))

    implementation(platform(libs.kotlinx.coroutines.bom))
    implementation(libs.jetbrains.kotlinx.coroutines.core)
    implementation(libs.jetbrains.kotlinx.coroutines.android)

    implementation(libs.androidx.datastore.preferences)
}


mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-datastores",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}