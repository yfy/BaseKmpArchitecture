import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jetbrains.kotlin.multiplatform")
            apply(plugin = "com.android.library")

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            extensions.configure<KotlinMultiplatformExtension> {
                explicitApi()

                androidTarget {
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_17)
                    }
                }
                iosX64()
                iosArm64()
                iosSimulatorArm64()

                applyDefaultHierarchyTemplate()

                sourceSets.apply {
                    getByName("commonMain").dependencies {
                        implementation(libs.findLibrary("kotlinx-coroutines-core").get())
                    }
                    getByName("commonTest").dependencies {
                        implementation(libs.findLibrary("kotlin-test").get())
                        implementation(libs.findLibrary("kotlinx-coroutines-test").get())
                    }
                }
            }

            extensions.configure<LibraryExtension> {
                compileSdk = libs.findVersion("android-compileSdk").get().toString().toInt()
                defaultConfig {
                    minSdk = libs.findVersion("android-minSdk").get().toString().toInt()
                }
                compileOptions {
                    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_17
                    targetCompatibility = org.gradle.api.JavaVersion.VERSION_17
                }
            }
        }
    }
}
