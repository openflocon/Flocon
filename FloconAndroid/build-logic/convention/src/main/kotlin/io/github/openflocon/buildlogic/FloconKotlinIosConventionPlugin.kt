package io.github.openflocon.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class FloconKotlinIosConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
            }

            extensions.configure<KotlinMultiplatformExtension> {
                iosArm64()
                iosSimulatorArm64()

                // kotlin.mpp.applyDefaultHierarchyTemplate=false means KGP only wires
                // <target>Main -> commonMain. Without this, src/iosMain is an orphan
                // source set that is silently never compiled.
                with(sourceSets) {
                    val common = getByName("commonMain")
                    val iosMain = maybeCreate("iosMain").apply { dependsOn(common) }
                    getByName("iosArm64Main").dependsOn(iosMain)
                    getByName("iosSimulatorArm64Main").dependsOn(iosMain)

                    findByName("commonTest")?.let { commonTest ->
                        val iosTest = maybeCreate("iosTest").apply { dependsOn(commonTest) }
                        getByName("iosArm64Test").dependsOn(iosTest)
                        getByName("iosSimulatorArm64Test").dependsOn(iosTest)
                    }
                }
            }
        }
    }
}
