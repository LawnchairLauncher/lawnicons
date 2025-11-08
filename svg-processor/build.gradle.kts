plugins {
    id("org.jetbrains.kotlin.jvm")
    application
}

application {
    mainClass = "app.lawnchair.lawnicons.helper.MainKt"
}

dependencies {
    implementation("com.android.tools:sdk-common:31.13.0")
    implementation("org.dom4j:dom4j:2.2.0")
    implementation("commons-io:commons-io:2.21.0")
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
