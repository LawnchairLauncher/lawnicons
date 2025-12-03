import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application") version "8.13.1" apply false
    id("org.jetbrains.kotlin.android") version "2.2.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21" apply false
    id("com.google.devtools.ksp") version "2.3.3" apply false
    id("com.google.dagger.hilt.android") version "2.57.2" apply false
    id("app.cash.licensee") version "1.14.1" apply false
    id("com.diffplug.spotless") version "8.1.0" apply false
}

allprojects {
    apply(plugin = "com.diffplug.spotless")
    extensions.configure<SpotlessExtension> {
        format("xml") {
            target("app/assets/appfilter.xml")
            eclipseWtp(EclipseWtpFormatterStep.XML).configFile("$rootDir/spotless.xml.prefs")
        }
        kotlin {
            target("src/**/*.kt")
            ktlint("1.8.0").customRuleSets(
                listOf(
                    "io.nlopez.compose.rules:ktlint:0.5.0",
                ),
            ).editorConfigOverride(
                mapOf(
                    "ktlint_compose_compositionlocal-allowlist" to "disabled",
                    "ktlint_compose_lambda-param-event-trailing" to "disabled",
                    "ktlint_compose_content-slot-reused" to "disabled",
                ),
            )
        }
        kotlinGradle {
            ktlint("1.8.0")
        }
    }

    plugins.withType<JavaBasePlugin>().configureEach {
        extensions.configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
}
