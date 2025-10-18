plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    id("com.vanniktech.maven.publish") version "0.34.0"
}

android {
    namespace = "io.github.openflocon.flocon"
    compileSdk = 36

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(platform(libs.kotlinx.coroutines.bom))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.jakewharton.process.phoenix)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    api(project(":flocon-base"))
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)

    if (project.hasProperty("signing.required") && project.property("signing.required") == "false") {
        // Skip signing
    } else {
        signAllPublications()
    }

    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )

    pom {
        name = "Flocon"
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