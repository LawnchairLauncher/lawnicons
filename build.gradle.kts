plugins {
    id("com.android.application") version "7.4.1" apply false
    id("com.android.library") version "7.4.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.sergei-lapin.napt") version "1.19" apply false
    id("com.google.dagger.hilt.android") version "2.45" apply false
    id("com.google.android.gms.oss-licenses-plugin") version "0.10.6" apply false
    id("org.jmailen.kotlinter") version "3.13.0" apply false
}

allprojects {
    apply(plugin = "org.jmailen.kotlinter")
}

tasks.register("clean") {
    delete(rootProject.buildDir)
}
