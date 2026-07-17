plugins {
    alias(libs.plugins.kmp.feature)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.playservices)
            implementation(libs.googleid)
        }
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.network)
            implementation(projects.core.datastore)
            implementation(projects.core.database)
            implementation(projects.core.analytics)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.lifecycle.viewmodel)
        }
        commonTest.dependencies {
            implementation(libs.koin.test)
        }
    }
}

android {
    namespace = "com.yfy.kmp.feature.auth"
}
