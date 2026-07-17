pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "BaseKmpArchitecture"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":core:model")
include(":core:common")
include(":core:network")
include(":core:designsystem")
include(":core:datastore")
include(":core:database")
include(":core:analytics")
include(":core:notification")
include(":core:ads")
include(":feature:auth")
include(":feature:onboarding")
include(":feature:profile")
include(":feature:settings")
include(":feature:paywall")
include(":arch-test")
include(":androidApp")
include(":shared")
