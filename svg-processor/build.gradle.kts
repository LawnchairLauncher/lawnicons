plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

application {
    mainClass = "app.lawnchair.lawnicons.helper.MainKt"
}

dependencies {
    implementation(libs.android.tools.sdk.common)
    implementation(libs.dom4j)
    implementation(libs.commons.io)
}

val svgDir = rootDir.resolve("svgs")
val resDir = rootDir.resolve("app/src/runtime/res")
val assetsDir = rootDir.resolve("app/assets")

tasks.run.configure {
    // Configure the inputs and outputs for this task, avoid unnecessary re-runs.
    inputs.dir(svgDir)
    inputs.dir(assetsDir)
    outputs.dir(resDir)

    args(
        svgDir,
        resDir,
        assetsDir,
    )
}

tasks.clean {
    // Delete the generated resources directory in every clean.
    delete(resDir)
}
