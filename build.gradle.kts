import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.licensee) apply false
    alias(libs.plugins.spotless) apply false
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
            ktlint(libs.ktlint.get().version).customRuleSets(
                listOf(
                    libs.composeRules.get().toString(),
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
            ktlint(libs.ktlint.get().version)
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
