plugins {
    alias(libs.plugins.kmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.datastore)
            implementation(projects.core.database)
        }
    }
}

android {
    namespace = "com.yfy.kmp.feature.profile"
}
