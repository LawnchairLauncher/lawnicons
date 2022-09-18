plugins {
    id("com.android.application") version "7.3.0" apply false
    id("com.android.library") version "7.3.0" apply false
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
    id("com.google.dagger.hilt.android") version "2.43.2" apply false
    id("com.google.android.gms.oss-licenses-plugin") version "0.10.5" apply false
}

allprojects {
    tasks.matching {
        it.name.contains("OssLicensesTask")
    }.configureEach {
        notCompatibleWithConfigurationCache("https://github.com/google/play-services-plugins/issues/206")
    }
}

tasks.register("clean") {
    delete(rootProject.buildDir)
}
