package app.lawnchair.lawnicons.helper

fun main() {
    val rootDir = ".."
    val sourceDir = "$rootDir/svgs/"
    val darkResDir = "$rootDir/app/src/dark/res"
    val lightResDir = "$rootDir/app/src/light/res"
    val appFilterFile = "$rootDir/app/assets/appfilter.xml"

    // Convert svg to drawable in runtime
    SvgFilesProcessor.process(sourceDir, "$darkResDir/drawable", "dark")
    SvgFilesProcessor.process(sourceDir, "$lightResDir/drawable", "light")

    // Read appfilter xml and create icon, drawable xml file.
    ConfigProcessor.loadAndCreateConfigs(appFilterFile, darkResDir, lightResDir)

    println("SvgToVector task completed")
}
