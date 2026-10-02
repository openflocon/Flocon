package io.github.openflocon.buildlogic

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureAndroidLibrary() {
    extensions.configure<KotlinMultiplatformExtension> {
        (this as ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryExtension>("androidLibrary") {
            compileSdk = 36
            minSdk = 23

//            withDeviceTestBuilder {
//                sourceSetTreeName = "test"
//            }.configure {
//                instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//            }
        }
    }
}
