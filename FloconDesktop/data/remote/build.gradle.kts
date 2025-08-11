plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)

    alias(libs.plugins.ktlint)
}

kotlin {
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutinesCore)
            implementation(libs.kotlinx.serializationJson)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        getByName("desktopMain").dependencies {
            implementation(libs.ktor.serverCore)
            implementation(libs.ktor.serverNetty)
            implementation(libs.ktor.serializationKotlinJson)
            implementation(libs.ktor.serializationKotlinJson)
            implementation(libs.ktor.serverContentNegociation)
            implementation(libs.ktor.serverWebsocket)
            implementation(libs.kotlinx.coroutinesCore)
            implementation(libs.kotlinx.serializationJson)
        }
    }

}
