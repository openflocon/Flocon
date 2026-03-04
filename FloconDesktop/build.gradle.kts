plugins {
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.aboutLibraries) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false

    alias(libs.plugins.buildconfig) apply false
}
