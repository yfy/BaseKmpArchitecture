import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.skie)
}

kotlin {
    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }

    val xcfName = "Shared"
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = xcfName
            isStatic = true
            export(projects.core.model)
            export(projects.core.common)
            export(projects.core.analytics)
            export(projects.core.datastore)
            export(projects.core.ads)
            export(projects.feature.auth)
            export(projects.feature.onboarding)
            export(projects.feature.profile)
            export(projects.feature.settings)
            export(projects.feature.paywall)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core.model)
            api(projects.core.common)
            api(projects.core.network)
            api(projects.core.datastore)
            api(projects.core.database)
            api(projects.core.analytics)
            api(projects.core.notification)
            api(projects.core.ads)
            api(projects.feature.auth)
            api(projects.feature.onboarding)
            api(projects.feature.profile)
            api(projects.feature.settings)
            api(projects.feature.paywall)
            api(libs.koin.core)
            implementation(libs.kermit)
            implementation(libs.kermit.crashlytics)
        }
        iosMain.dependencies {
            implementation(libs.crashkios.crashlytics)
        }
    }
}

skie {
    analytics {
        enabled.set(false)
    }
}

android {
    namespace = "com.yfy.kmp.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Destination must stay outside the `iosApp/iosApp` source root: Xcode 16 synchronized groups scan
// that root and would flatten these files into the bundle root, breaking the `MockResources/mock/`
// bundle path the pbxproj folder reference provides.
val syncMockJsonIos by tasks.registering(Copy::class) {
    from(rootProject.layout.projectDirectory.dir("mock-resources/mock"))
    into(rootProject.layout.projectDirectory.dir("iosApp/MockResources/mock"))
}
tasks.matching { it.name.startsWith("embedAndSign") }.configureEach {
    dependsOn(syncMockJsonIos)
    dependsOn(rootProject.tasks.named("generateDesignTokens"))
}
