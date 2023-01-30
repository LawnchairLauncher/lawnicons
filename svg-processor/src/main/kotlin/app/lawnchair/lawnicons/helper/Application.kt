package app.lawnchair.lawnicons.helper

fun main() {
    val rootDir = ".."
    val sourceDir = "$rootDir/svgs/"
    val resDir = "$rootDir/app/src/runtime/res"
    val appFilterFile = "$rootDir/app/assets/appfilter.xml"

    // Convert svg to drawable in runtime
    SvgFilesProcessor.process(sourceDir, "$resDir/drawable")

    // Read appfilter xml and create icon, drawable xml file.
    ConfigProcessor.loadAndCreateConfigs(appFilterFile, resDir)

    println("SvgToVector task completed")
}
