plugins {
    id("papermixins.java-conventions")
}

dependencies {
    compileOnly(libs.fabric.mixins)
    copyServices(libs.mixinextras.common)

    compileOnly(project(":plugin-mixin-interop"))
}