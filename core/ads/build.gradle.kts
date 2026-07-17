plugins {
    alias(libs.plugins.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.kermit)
            api(projects.core.common)
        }
        androidMain.dependencies {
            implementation(libs.play.services.ads)
            implementation(libs.user.messaging.platform)
        }
    }
}

android {
    namespace = "com.yfy.kmp.core.ads"
}
