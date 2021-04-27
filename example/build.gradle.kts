plugins {
    java
    id("org.jetbrains.dokka.download")
}


repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") {
        content {
            includeGroup("org.jetbrains.kotlinx")
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
}

downloadDependencies {
    outputDir.set(buildDir.resolve("dokka"))
    allowedRepositories.set(listOf("https://repo.maven.apache.org/maven2/", "https://dl.google.com/dl/android/maven2/"))
}
