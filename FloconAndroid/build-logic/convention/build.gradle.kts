import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

group = "io.github.openflocon.buildlogic"

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.vanniktech.mavenPublish.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("floconKotlinMultiplatform") {
            id = "flocon.kotlin.library"
            implementationClass = "io.github.openflocon.buildlogic.FloconKotlinMultiplatformConventionPlugin"
        }

        register("floconAndroidLibrary") {
            id = "flocon.android.library"
            implementationClass = "io.github.openflocon.buildlogic.FloconAndroidLibraryConventionPlugin"
        }
        register("floconIosLibrary") {
            id = "flocon.ios.library"
            implementationClass = "io.github.openflocon.buildlogic.FloconKotlinIosConventionPlugin"
        }
        register("floconJvmLibrary") {
            id = "flocon.jvm.library"
            implementationClass = "io.github.openflocon.buildlogic.FloconKotlinJvmConventionPlugin"
        }
        register("floconWasmLibrary") {
            id = "flocon.wasm.library"
            implementationClass = "io.github.openflocon.buildlogic.FloconKotlinWasmConventionPlugin"
        }

        register("floconPublish") {
            id = "flocon.publish"
            implementationClass = "io.github.openflocon.buildlogic.FloconPublishConventionPlugin"
        }
    }
}
