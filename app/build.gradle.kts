import app.cash.licensee.LicenseeTask
import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import com.android.build.gradle.tasks.MergeResources
import java.io.FileInputStream
import java.util.Locale
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("app.cash.licensee")
    id("org.gradle.android.cache-fix")
}

val buildCommit = providers.exec {
    commandLine("git", "rev-parse", "--short=7", "HEAD")
}.standardOutput.asText.get().trim()

val ciBuild = providers.environmentVariable("CI").isPresent
val ciRef = providers.environmentVariable("GITHUB_REF").orNull.orEmpty()
val ciRunNumber = providers.environmentVariable("GITHUB_RUN_NUMBER").orNull.orEmpty()
val isReleaseBuild = ciBuild && ciRef.contains("main")
val devReleaseName = if (ciBuild) "(Dev #$ciRunNumber)" else "($buildCommit)"

val version = "2.7.0"
val versionDisplayName = "$version ${if (isReleaseBuild) "" else devReleaseName}"

android {
    compileSdk = 34
    namespace = "app.lawnchair.lawnicons"

    defaultConfig {
        applicationId = "app.lawnchair.lawnicons"
        minSdk = 26
        targetSdk = 34
        versionCode = 10
        versionName = versionDisplayName
        vectorDrawables.useSupportLibrary = true
    }

    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val releaseSigning = if (keystorePropertiesFile.exists()) {
        val keystoreProperties = Properties()
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
        signingConfigs.create("release") {
            keyAlias = keystoreProperties["keyAlias"].toString()
            keyPassword = keystoreProperties["keyPassword"].toString()
            storeFile = rootProject.file(keystoreProperties["storeFile"].toString())
            storePassword = keystoreProperties["storePassword"].toString()
        }
    } else {
        signingConfigs["debug"]
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = releaseSigning
            proguardFiles("proguard-rules.pro")
        }
        debug {
            signingConfig = releaseSigning
        }
    }

    flavorDimensions += "product"
    productFlavors {
        create("app") {
            dimension = "product"
            resValue("string", "apps_name", "Lawnicons")
        }
    }
    sourceSets.getByName("app") {
        res.setSrcDirs(listOf("src/runtime/res"))
    }

    buildFeatures {
        buildConfig = true
        compose = true
        resValues = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    androidComponents.onVariants { variant ->
        val capName = variant.name.replaceFirstChar { it.titlecase(Locale.ROOT) }
        val licenseeTask = tasks.named<LicenseeTask>("licenseeAndroid$capName")
        val copyArtifactsTask = tasks.register<Copy>("copy${capName}Artifacts") {
            dependsOn(licenseeTask)
            from(licenseeTask.map { it.outputDir.file("artifacts.json") })
            into(layout.buildDirectory.dir("generated/dependencyAssets/${variant.name}"))
        }
        variant.sources.assets?.addGeneratedSourceDirectory(licenseeTask) {
            objects.directoryProperty().fileProvider(copyArtifactsTask.map { it.destinationDir })
        }
    }

    applicationVariants.all {
        outputs.all {
            (this as? ApkVariantOutputImpl)?.outputFileName =
                "Lawnicons $versionName v${versionCode}_${buildType.name}.apk"
        }
    }
}

// Process SVGs before every build.
tasks.withType<MergeResources>().configureEach {
    dependsOn(projects.svgProcessor.dependencyProject.tasks.named("run"))
}

licensee {
    allow("Apache-2.0")
}

dependencies {
    val lifecycleVersion = "2.7.0"
    val hiltVersion = "2.51"

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.02"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-util")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("io.github.fornewid:material-motion-compose-core:1.2.0")
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("io.coil-kt:coil-compose:2.6.0")
    val retrofitVersion = "2.10.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:$retrofitVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
}
