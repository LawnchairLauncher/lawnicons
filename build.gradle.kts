import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep

plugins {
    id("com.android.application") version "8.1.1" apply false
    id("com.android.library") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10" apply false
    id("com.sergei-lapin.napt") version "1.19" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("app.cash.licensee") version "1.7.0" apply false
    id("com.diffplug.spotless") version "6.21.0" apply false
}

allprojects {
    plugins.withType<JavaBasePlugin>().configureEach {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion = JavaLanguageVersion.of(17)
            }
        }
    }

    apply(plugin = "com.diffplug.spotless")
    extensions.configure<SpotlessExtension> {
        format("xml") {
            eclipseWtp(EclipseWtpFormatterStep.XML).configFile("$rootDir/spotless.xml.prefs")
            target("app/assets/appfilter.xml")
        }
        kotlin {
            ktlint()
            target("src/**/*.kt")
        }
        kotlinGradle {
            ktlint()
            target("*.kts")
        }
    }
}
