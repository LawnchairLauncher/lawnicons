import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import java.io.FileInputStream
import java.util.Locale
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.sergei-lapin.napt")
    id("dagger.hilt.android.plugin")
    id("app.cash.licensee")
}

val buildCommit = providers.exec {
    commandLine("git", "rev-parse", "--short=7", "HEAD")
}.standardOutput.asText.get().trim()

val ciBuild = System.getenv("CI") == "true"
val ciRef = System.getenv("GITHUB_REF").orEmpty()
val ciRunNumber = System.getenv("GITHUB_RUN_NUMBER").orEmpty()
val isReleaseBuild = ciBuild && ciRef.contains("main")
val devReleaseName = if (ciBuild) "(Dev #$ciRunNumber)" else "($buildCommit)"

val version = "2.2.1"
val versionDisplayName = "$version ${if (isReleaseBuild) "" else devReleaseName}"

android {
    compileSdk = 34
    namespace = "app.lawnchair.lawnicons"

    defaultConfig {
        applicationId = "app.lawnchair.lawnicons"
        minSdk = 26
        targetSdk = 34
        versionCode = 5
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
        assets.srcDir(layout.buildDirectory.dir("generated/dependencyAssets/"))
        res.setSrcDirs(listOf("src/runtime/res"))
    }

    buildFeatures {
        buildConfig = true
        compose = true
        resValues = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
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
        val variantName = name
        val capitalizedName =
            name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val copyArtifactList =
            tasks.register<Copy>("copy${capitalizedName}ArtifactList") {
                dependsOn(tasks.named("licensee$capitalizedName"))
                from(reporting.file("licensee/$variantName/artifacts.json"))
                into(layout.buildDirectory.dir("generated/dependencyAssets/"))
            }
        tasks.named("merge${capitalizedName}Assets").configure {
            dependsOn(copyArtifactList)
        }
        if (buildType.name == "release") {
            tasks.named("lintVitalAnalyze$capitalizedName").configure {
                dependsOn(copyArtifactList)
            }
        }

        outputs.all {
            (this as? ApkVariantOutputImpl)?.outputFileName =
                "Lawnicons $versionName v${versionCode}_${buildType.name}.apk"
        }
    }
}

hilt.enableAggregatingTask = false

licensee {
    allow("Apache-2.0")
}

dependencies {
    val lifecycleVersion = "2.6.1"
    val hiltVersion = "2.47"

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-util")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.navigation:navigation-compose:2.7.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
    implementation("io.github.fornewid:material-motion-compose-core:1.0.6")
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    annotationProcessor("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}
