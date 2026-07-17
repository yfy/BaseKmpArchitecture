plugins {
    alias(libs.plugins.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
            api(libs.androidx.lifecycle.viewmodel)
            api(libs.koin.core)
            api(projects.core.datastore)
            api(projects.core.database)
            api(projects.core.analytics)
            implementation(libs.kermit)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.security.crypto)
        }
        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "com.yfy.kmp.core.common"
}
