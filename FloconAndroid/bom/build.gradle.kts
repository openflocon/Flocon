import com.vanniktech.maven.publish.JavaPlatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    `java-platform`
    id("flocon.publish")
}

// every published Flocon module (plugins and their no-op) must be known before the constraints are built
rootProject.subprojects
    .filter { it.path != project.path && it.path != ":sample" && !it.path.startsWith(":sample:") && it.path != ":sample-multiplatform" }
    .forEach { evaluationDependsOn(it.path) }

dependencies {
    constraints {
        rootProject.subprojects
            .filter { it.path != project.path && it.pluginManager.hasPlugin("com.vanniktech.maven.publish") }
            .sortedBy { it.path }
            .forEach { api(project(it.path)) }
    }
}

extensions.configure<MavenPublishBaseExtension> {
    configure(JavaPlatform())

    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon-bom",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String,
    )

    pom {
        name = "Flocon BOM"
        description = "Bill of Materials aligning the versions of all Flocon SDK modules."
    }
}
