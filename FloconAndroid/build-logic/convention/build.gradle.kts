plugins {
    `kotlin-dsl`
}

group = "io.github.openflocon.buildlogic"

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.vanniktech.mavenPublish.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("floconAndroidLibrary") {
            id = "flocon.android.library"
            implementationClass = "io.github.openflocon.buildlogic.FloconAndroidLibraryConventionPlugin"
        }
        register("floconKotlinMultiplatform") {
            id = "flocon.kotlin.multiplatform"
            implementationClass = "io.github.openflocon.buildlogic.FloconKotlinMultiplatformConventionPlugin"
        }
        register("floconPublish") {
            id = "flocon.publish"
            implementationClass = "io.github.openflocon.buildlogic.FloconPublishConventionPlugin"
        }
    }
}
