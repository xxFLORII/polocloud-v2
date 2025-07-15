plugins {
    id("java")
}

dependencies {
    compileOnly(libs.velocity)
    compileOnly(libs.waterdog)
    annotationProcessor(libs.velocity)
    annotationProcessor(libs.waterdog)

    compileOnly(project(":polocloud-api"))

    compileOnly(libs.bundles.utils)
    annotationProcessor(libs.bundles.utils)
}

tasks.jar {
    archiveFileName.set("polocloud-java-bridge-${version}.jar")
}
