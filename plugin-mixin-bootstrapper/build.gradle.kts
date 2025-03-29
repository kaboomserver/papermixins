plugins {
    id("papermixins.java-conventions")
}

dependencies {
    compileOnly(libs.mixin)
    copyServices(libs.mixinextras)

    compileOnly(project(":plugin-mixin-interop"))
    compileOnly(libs.slf4j.api)
}