plugins {
    id("com.android.application") version "7.3.1" apply false
    id("com.android.library") version "7.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply false
    id("com.sergei-lapin.napt") version "1.17" apply false
    id("com.google.dagger.hilt.android") version "2.44.2" apply false
    id("com.google.android.gms.oss-licenses-plugin") version "0.10.5" apply false
    id("org.jmailen.kotlinter") version "3.12.0" apply false
}

allprojects {
    apply(plugin = "org.jmailen.kotlinter")

    tasks.matching {
        it.name.contains("OssLicensesTask")
    }.configureEach {
        notCompatibleWithConfigurationCache("https://github.com/google/play-services-plugins/issues/206")
    }
}

tasks.register("clean") {
    delete(rootProject.buildDir)
}
