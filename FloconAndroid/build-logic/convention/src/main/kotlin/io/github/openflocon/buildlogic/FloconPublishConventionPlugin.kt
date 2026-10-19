package io.github.openflocon.buildlogic

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class FloconPublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.vanniktech.maven.publish")
            }

            extensions.configure<MavenPublishBaseExtension> {
                publishToMavenCentral(automaticRelease = true)

                // -Psigning.required=false to publish locally without a signing key
                if (findProperty("signing.required") != "false") {
                    signAllPublications()
                }

                // the name is set by each module
                pom {
                    description.set(findProperty("floconDescription") as? String)
                    inceptionYear.set("2025")
                    url.set("https://github.com/openflocon/Flocon")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                            distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("openflocon")
                            name.set("Open Flocon")
                            url.set("https://github.com/openflocon")
                        }
                    }
                    scm {
                        url.set("https://github.com/openflocon/Flocon")
                        connection.set("scm:git:git://github.com/openflocon/Flocon.git")
                        developerConnection.set("scm:git:ssh://git@github.com/openflocon/Flocon.git")
                    }
                }
            }
        }
    }
}
