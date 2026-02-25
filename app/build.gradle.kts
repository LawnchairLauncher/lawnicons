import app.cash.licensee.SpdxId
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.licensee)
    alias(libs.plugins.metro)
}

val buildCommit = providers.exec {
    commandLine("git", "rev-parse", "--short=7", "HEAD")
}.standardOutput.asText.get().trim()

val ciBuild = providers.environmentVariable("CI").isPresent
val ciRef = providers.environmentVariable("GITHUB_REF").orNull.orEmpty()
val ciRunNumber = providers.environmentVariable("GITHUB_RUN_NUMBER").orNull.orEmpty()
val isReleaseBuild = ciBuild && ciRef.contains("main")
val devReleaseName = if (ciBuild) "(Dev #$ciRunNumber)" else "($buildCommit)"

val version = "2.17.1"
val versionDisplayName = version + if (!isReleaseBuild) " $devReleaseName" else ""

android {
    compileSdk = 36
    namespace = "app.lawnchair.lawnicons"

    defaultConfig {
        applicationId = "app.lawnchair.lawnicons"
        minSdk = 26
        targetSdk = compileSdk
        versionCode = 25
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
        res.directories.add("src/runtime/res")
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            // TODO: https://github.com/android/gradle-recipes/blob/cbe7c7dea2a3f5b1764756f24bf453d1235c80e2/listenToArtifacts/README.md
            with(output as com.android.build.api.variant.impl.VariantOutputImpl) {
                val newApkName = "Lawnicons ${versionName.get()} v${versionCode.get()}_${variant.buildType}.apk"
                outputFileName = newApkName
            }
        }
    }
}

composeCompiler {
    stabilityConfigurationFiles.addAll(
        layout.projectDirectory.file("compose_compiler_config.conf"),
    )
    reportsDestination = layout.buildDirectory.dir("compose_build_reports")
}

licensee {
    allow(SpdxId.Apache_20)
    allow(SpdxId.MIT)

    bundleAndroidAsset = true
    androidAssetReportPath = "licenses.json"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.util)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowsizeclass)
    implementation(libs.androidx.graphics.shapes)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.metrox.viewmodel.compose)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)

    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.lazycolumn.scrollbar)
    implementation(libs.material.motion.compose.core)
}

tasks.preBuild {
    dependsOn(project(projects.svgProcessor.path).tasks.named("run"))
}
