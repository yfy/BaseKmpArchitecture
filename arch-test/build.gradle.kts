plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation(libs.konsist)
    testImplementation(libs.kotlin.test)
}

tasks.test {
    useJUnitPlatform()
    inputs.files(
        fileTree(rootDir) {
            include("**/src/**/*.kt")
            exclude("**/build/**")
        }
    ).withPropertyName("repoKotlinSources").withPathSensitivity(PathSensitivity.RELATIVE)
}
