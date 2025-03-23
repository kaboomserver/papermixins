plugins {
    id("checkstyle")
    id("java-library")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
}

group = "pw.kaboom"
version = "master"

base {
    archivesName = "paper-mixins"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.fabricmc.net/")
}

dependencies {
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")

    compileOnly("space.vectrix.ignite:ignite-api:1.1.0")
    compileOnly("net.fabricmc:sponge-mixin:0.15.2+mixin.0.8.7")
    compileOnly("io.github.llamalad7:mixinextras-common:0.4.1")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}
