plugins {
    alias(libs.plugins.kmp.library)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

android {
    namespace = "com.yfy.kmp.core.datastore"
}
