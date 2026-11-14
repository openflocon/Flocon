package io.github.openflocon.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class FloconKotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
            }

            extensions.configure<KotlinMultiplatformExtension> {
                compilerOptions {
                    freeCompilerArgs.add("-XXLanguage:+ExpectRefinement")
                    // the Flocon modules share an internal API, the samples and the apps don't use it
                    if (!path.startsWith(":sample")) {
                        optIn.add("io.github.openflocon.flocon.InternalFloconApi")
                    }
                }
            }
        }
    }
}
