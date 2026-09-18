plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.vanniktech.maven.publish) apply false
    alias(libs.plugins.protobuf) apply false
}

subprojects {
    plugins.withId("com.vanniktech.maven.publish") {
        configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
            publishToMavenCentral(automaticRelease = true)

            if (project.hasProperty("signing.required") && project.property("signing.required") == "false") {
                // Skip signing
            } else {
                signAllPublications()
            }

            pom {
                description = project.property("floconDescription") as String
                inceptionYear = "2025"
                url = "https://github.com/openflocon/Flocon"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                        distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "openflocon"
                        name = "Open Flocon"
                        url = "https://github.com/openflocon"
                    }
                }
                scm {
                    url = "https://github.com/openflocon/Flocon"
                    connection = "scm:git:git://github.com/openflocon/Flocon.git"
                    developerConnection = "scm:git:ssh://git@github.com/openflocon/Flocon.git"
                }
            }
        }
    }
}