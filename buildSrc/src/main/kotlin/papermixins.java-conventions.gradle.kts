plugins {
    id("java-library")
    id("checkstyle")
}

group = "pw.kaboom"
version = "master"

val includeInJar by configurations.creating {
    isTransitive = false
}

configurations {
    compileClasspath {
        extendsFrom(includeInJar)
    }
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.processResources {
    dependsOn(includeInJar)

    includeInJar.forEach { file -> from(file.path) { rename { "META-INF/jars/${file.name}" } } }
}
