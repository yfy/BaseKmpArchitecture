plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.gms)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.yfy.kmp.android"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        // TODO(template): replace before release — application id (also update namespace, google-services.json and assetlinks.json).
        applicationId = "com.yfy.kmp.android"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0"
    }

    flavorDimensions += "environment"
    productFlavors {
        create("mock") {
            dimension = "environment"
            applicationIdSuffix = ".mock"
            buildConfigField("String", "APP_ENVIRONMENT", "\"MOCK\"")
        }
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            buildConfigField("String", "APP_ENVIRONMENT", "\"DEBUG\"")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "APP_ENVIRONMENT", "\"PROD\"")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // TODO(template): replace before release — debug signing keeps local release builds installable; use your real signingConfig.
            signingConfig = signingConfigs.getByName("debug")
            // TODO(template): replace before release — flip to true once real Firebase config is in place so crash reports get deobfuscated.
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    sourceSets["main"].assets.srcDir(layout.buildDirectory.dir("generated/mockAssets"))

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

val mockAssetsDir = layout.buildDirectory.dir("generated/mockAssets")
val syncMockJson by tasks.registering(Copy::class) {
    from(rootProject.layout.projectDirectory.dir("mock-resources/mock"))
    into(mockAssetsDir.map { it.dir("mock") })
}
tasks.named("preBuild") { dependsOn(syncMockJson) }

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(projects.feature.auth)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.profile)
    implementation(projects.feature.settings)
    implementation(projects.feature.paywall)
    implementation(projects.core.ads)
    implementation(projects.shared)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.play.services.ads)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
