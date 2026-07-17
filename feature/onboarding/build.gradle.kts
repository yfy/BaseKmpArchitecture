plugins {
    alias(libs.plugins.kmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.datastore)
        }
    }
}

android {
    namespace = "com.yfy.kmp.feature.onboarding"
}
