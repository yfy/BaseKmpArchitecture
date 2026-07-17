plugins {
    alias(libs.plugins.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.kermit)
        }
        androidMain.dependencies {
            implementation(libs.firebase.analytics)
        }
    }
}

android {
    namespace = "com.yfy.kmp.core.analytics"
}
