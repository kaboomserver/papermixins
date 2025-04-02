plugins {
    id("papermixins.java-conventions")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
    id("com.gradleup.shadow") version "9.0.0-beta11"
}

base {
    archivesName = "paper-mixins"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.fabricmc.net/")
    maven("https://repo.essentialsx.net/releases/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    compileOnly("space.vectrix.ignite:ignite-api:1.1.0")

    compileOnly(libs.mixin)
    compileOnly(libs.mixinextras)
    annotationProcessor(libs.mixinextras)

    compileOnly("net.essentialsx:EssentialsX:2.21.0") { isTransitive = false }
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.13.0") { isTransitive = false }
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    jar {
        archiveClassifier.set("original")
    }

    shadowJar {
        archiveClassifier.set("")
    }
}