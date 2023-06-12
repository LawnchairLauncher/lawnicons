plugins {
    id("com.android.application") version "8.0.2" apply false
    id("com.android.library") version "8.0.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.21" apply false
    id("com.sergei-lapin.napt") version "1.19" apply false
    id("com.google.dagger.hilt.android") version "2.46.1" apply false
    id("app.cash.licensee") version "1.7.0" apply false
    id("org.jmailen.kotlinter") version "3.15.0" apply false
}

allprojects {
    apply(plugin = "org.jmailen.kotlinter")
}

tasks.register("clean") {
    delete(rootProject.buildDir)
}
