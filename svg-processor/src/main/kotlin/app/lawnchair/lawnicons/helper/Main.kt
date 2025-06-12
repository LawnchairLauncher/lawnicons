/*
 * Copyright 2024 Lawnchair Launcher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.lawnchair.lawnicons.helper

fun main(args: Array<String>) {
    val (svgDir, resDir, customTag, assetsDir) = args
    val appFilterFile = "$assetsDir/appfilter.xml"
    val previousAppFilterFile = "$resDir/xml/appfilter_previous.xml"

    // Convert svg to drawable in runtime
    SvgFilesProcessor.process(svgDir, "$resDir/drawable")
    println("SvgToVectorDrawable task completed")

    // Read appfilter xml and create icon, drawable xml file.
    ConfigProcessor.loadAndCreateConfigs(appFilterFile, resDir)
    println("ConfigProcessor task completed")

    AppfilterDiffCreator.createAppfilterDiff(resDir, customTag, appFilterFile, previousAppFilterFile)
    println("Appfilter diff task completed")
}
