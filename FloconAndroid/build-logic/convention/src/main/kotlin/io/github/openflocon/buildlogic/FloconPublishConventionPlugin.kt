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

                if (project.hasProperty("signing.required") && project.property("signing.required") == "false") {
                    // Skip signing
                } else {
                    signAllPublications()
                }

                pom {
                    name.set(project.name)
                    description.set(project.findProperty("floconDescription") as? String)
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
