buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.40.5")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.4")
    }
}

plugins {
    id("com.android.application") version "7.1.0" apply false
    id("com.android.library") version "7.1.0" apply false
    id("org.jetbrains.kotlin.android") version Versions.KOTLIN apply false
}

tasks.register("clean") {
    delete(rootProject.buildDir)
}
