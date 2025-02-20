import app.cash.licensee.LicenseeTask
import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import com.android.build.gradle.tasks.MergeResources
import java.util.Locale
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
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

val version = "2.13.1"
val versionDisplayName = version + if (!isReleaseBuild) " $devReleaseName" else ""

android {
    compileSdk = 35
    namespace = "app.lawnchair.lawnicons"

    defaultConfig {
        applicationId = "app.lawnchair.lawnicons"
        minSdk = 26
        targetSdk = compileSdk
        versionCode = 18
        versionName = versionDisplayName
        vectorDrawables.useSupportLibrary = true
    }

    androidResources {
        generateLocaleConfig = true
    }

    val releaseSigning = try {
        val keystoreProperties = Properties()
        keystoreProperties.load(rootProject.file("keystore.properties").inputStream())
        signingConfigs.create("release") {
            keyAlias = keystoreProperties["keyAlias"].toString()
            keyPassword = keystoreProperties["keyPassword"].toString()
            storeFile = rootProject.file(keystoreProperties["storeFile"].toString())
            storePassword = keystoreProperties["storePassword"].toString()
        }
    } catch (ignored: Exception) {
        signingConfigs["debug"]
    }

    buildTypes {
        all {
            signingConfig = releaseSigning
            isPseudoLocalesEnabled = true
        }
        release {
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
        }
        create("play") {
            applicationIdSuffix = ".play"
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
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
        resValues = true
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

    applicationVariants.all {
        outputs.all {
            (this as? ApkVariantOutputImpl)?.outputFileName =
                "Lawnicons $versionName v${versionCode}_${buildType.name}.apk"
        }
    }
}

androidComponents.onVariants { variant ->
    val capName = variant.name.replaceFirstChar { it.titlecase(Locale.ROOT) }
    val licenseeTask = tasks.named<LicenseeTask>("licenseeAndroid$capName")
    val copyArtifactsTask = tasks.register<Copy>("copy${capName}Artifacts") {
        dependsOn(licenseeTask)
        from(licenseeTask.map { it.jsonOutput })
        // Copy artifacts.json to a new directory.
        into(layout.buildDirectory.dir("generated/dependencyAssets/${variant.name}"))
    }
    variant.sources.assets?.addGeneratedSourceDirectory(licenseeTask) {
        // Avoid using LicenseeTask::outputDir as it contains extra files that we don't need.
        objects.directoryProperty().fileProvider(copyArtifactsTask.map { it.destinationDir })
    }
}

// Process SVGs before every build.
tasks.withType<MergeResources>().configureEach {
    dependsOn(projects.svgProcessor.dependencyProject.tasks.named("run"))
}

composeCompiler {
    stabilityConfigurationFile = layout.projectDirectory.file("compose_compiler_config.conf")
    reportsDestination = layout.buildDirectory.dir("compose_build_reports")
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation(platform("androidx.compose:compose-bom:2025.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-util")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material:material-icons-core-android")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.navigation:navigation-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    val hiltVersion = "2.55"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    val retrofitVersion = "2.11.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:$retrofitVersion")

    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.github.nanihadesuka:LazyColumnScrollbar:2.2.0")
    implementation("io.github.fornewid:material-motion-compose-core:2.0.1")
}
