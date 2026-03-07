plugins {
    id("checkstyle")
    id("java-library")
    alias(libs.plugins.paper.userdev)
}

group = "pw.kaboom"
version = "master"

base {
    archivesName = "paper-mixins"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.fabricmc.net/")

    maven("https://repo.essentialsx.net/snapshots/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.opencollab.dev/main/")
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper.api.get())

    compileOnly(libs.ignite)
    compileOnly(libs.mixin)
    compileOnly(libs.mixinextras)
    annotationProcessor(libs.mixinextras)

    compileOnly(libs.essentialsx) { isTransitive = false }
    compileOnly(libs.fastasyncworldedit) { isTransitive = false }
    compileOnly(libs.cloudburstmc.protocol) { isTransitive = false }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}
