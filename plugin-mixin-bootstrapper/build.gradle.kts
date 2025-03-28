plugins {
    id("papermixins.java-conventions")
}

dependencies {
    compileOnly(libs.fabric.mixins)
    compileOnly(project(":plugin-mixin-interop"))
}