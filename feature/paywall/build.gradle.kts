plugins {
    alias(libs.plugins.kmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        androidMain.dependencies {
            implementation(libs.revenuecat.purchases)
        }
    }
}

android {
    namespace = "com.yfy.kmp.feature.paywall"
}
