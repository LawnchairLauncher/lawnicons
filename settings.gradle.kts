pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
    }
}

// https://docs.gradle.com/enterprise/gradle-plugin/
plugins {
    id("com.gradle.enterprise") version "3.15.1"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlways()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "Lawnicons"

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

include(
    ":app",
    ":svg-processor",
)
