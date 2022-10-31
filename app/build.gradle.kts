import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.sergei-lapin.napt")
    id("dagger.hilt.android.plugin")
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    compileSdk = 33
    namespace = "app.lawnchair.lawnicons"

    defaultConfig {
        applicationId = "app.lawnchair.lawnicons"
        minSdk = 26
        targetSdk = 31
        versionCode = 2
        versionName = "1.1.0"
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }

    packagingOptions {
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
                "Lawnicons_${versionName}_${versionCode}_${buildType.name}.apk"
        }
    }
}

hilt.enableAggregatingTask = false

dependencies {
    val lifecycleVersion = "2.5.1"
    val composeVersion = "1.3.0"
    val accompanistVersion = "0.27.0"
    val hiltVersion = "2.44"
    val retrofitVersion = "2.9.0"

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.animation:animation:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.material3:material3:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("com.google.accompanist:accompanist-insets:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-navigation-animation:$accompanistVersion")
    implementation("io.github.fornewid:material-motion-compose-core:0.10.3")
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    annotationProcessor("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("com.github.LawnchairLauncher:oss-notices:1.0.2")
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
}
