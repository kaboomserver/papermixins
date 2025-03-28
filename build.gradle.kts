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
}

dependencies {
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")

    compileOnly("space.vectrix.ignite:ignite-api:1.1.0")

    includeInJar(libs.fabric.mixins)

    includeInJar(project(":plugin-mixin-bootstrapper"))
    implementation(project(":plugin-mixin-interop"))

    compileOnly("io.github.llamalad7:mixinextras-common:0.4.1")

    compileOnly("net.essentialsx:EssentialsX:2.20.1") { isTransitive = false }
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