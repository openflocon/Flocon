plugins {
    id("flocon.kotlin.library")
    id("flocon.android.library")
    id("flocon.ios.library")
    id("flocon.jvm.library")
    id("flocon.publish")
}

kotlin {
    android {
        namespace = "io.github.openflocon.flocon.database.sqldelight"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.flocon.flocon)
                api(projects.database.core.core)
                // the app has it (its generated code needs it): no need to bring a version
                implementation(libs.sqldelight.runtime)

                implementation(dependencies.platform(libs.kotlinx.coroutines.bom))
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        named("jvmTest") {
            dependencies {
                implementation(libs.sqldelight.sqlite.driver)
            }
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-database-sqldelight",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
        name = "Flocon Database SQLDelight"
    }
}
