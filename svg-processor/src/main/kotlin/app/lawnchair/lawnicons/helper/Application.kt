package app.lawnchair.lawnicons.helper

fun main() {
    val rootDir = ".."
    val sourceDir = "$rootDir/svgs/"
    val darkResDir = "$rootDir/app/src/dark/res"
    val appFilterFile = "$rootDir/app/assets/appfilter.xml"

    // Convert svg to drawable in runtime
    SvgFilesProcessor.process(sourceDir, "$darkResDir/drawable", "dark")

    // Read appfilter xml and create icon, drawable xml file.
    ConfigProcessor.loadAndCreateConfigs(appFilterFile, darkResDir)

    println("SvgToVector task completed")
}
