package app.lawnchair.lawnicons.helper

import com.android.ide.common.vectordrawable.Svg2Vector
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.FileVisitOption
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.util.EnumSet
import org.apache.commons.io.FileUtils
import org.dom4j.Document
import org.dom4j.DocumentException
import org.dom4j.DocumentHelper
import org.dom4j.io.OutputFormat
import org.dom4j.io.XMLWriter

object SvgFilesProcessor {
    private lateinit var sourceSvgPath: Path
    private lateinit var destinationVectorPath: Path
    private lateinit var mode: String

    fun process(sourceDirectory: String, destDirectory: String, mode: String) {
        this.sourceSvgPath = Paths.get(sourceDirectory)
        this.destinationVectorPath = Paths.get(destDirectory)
        this.mode = mode
        try {
            val options = EnumSet.of(FileVisitOption.FOLLOW_LINKS)
            // check first if source is a directory
            if (Files.isDirectory(sourceSvgPath)) {
                Files.walkFileTree(sourceSvgPath, options, Int.MAX_VALUE, fileVisitor)
            } else {
                println("source not a directory")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private val fileVisitor = object : FileVisitor<Path> {
        override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult =
            FileVisitResult.CONTINUE

        override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes?): FileVisitResult {
            // Skip folder which is processing svgs to xml
            if (dir == destinationVectorPath) {
                return FileVisitResult.SKIP_SUBTREE
            }
            val newDirectory = destinationVectorPath.resolve(sourceSvgPath.relativize(dir))
            try {
                Files.createDirectories(newDirectory)
            } catch (e: FileAlreadyExistsException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
                return FileVisitResult.SKIP_SUBTREE
            }
            return FileVisitResult.CONTINUE
        }

        override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
            convertToVector(file, destinationVectorPath.resolve(sourceSvgPath.relativize(file)))
            return FileVisitResult.CONTINUE
        }

        override fun visitFileFailed(file: Path, exc: IOException?): FileVisitResult =
            FileVisitResult.CONTINUE
    }

    private fun convertToVector(svgSource: Path, vectorTargetPath: Path) {
        // convert only if it is .svg
        if (svgSource.fileName.toString().endsWith(".svg")) {
            val targetFile = XmlUtil.getFileWithExtension(vectorTargetPath)
            val fileOutputStream = FileOutputStream(targetFile)
            Svg2Vector.parseSvgToXml(svgSource.toFile(), fileOutputStream)
            val fg = if (mode == "dark") "#000" else "#fff"
            val bg = if (mode == "dark") "@color/white" else "@color/black"
            try {
                updateXmlPath(targetFile, "android:strokeColor", fg)
                updateXmlPath(targetFile, "android:fillColor", fg)
            } catch (e: DocumentException) {
                throw RuntimeException(e)
            }
            createAdaptive(targetFile, bg)
        } else {
            println("Skipping file as its not svg " + svgSource.fileName)
        }
    }
    @Throws(IOException::class)
    private fun createAdaptive(xmlPath: String, bgColor: String) {
        val forgroundXml = xmlPath.replace(".xml", "_foreground.xml")
        val foregroundFile = FileUtils.getFile(forgroundXml)
        foregroundFile.delete()
        FileUtils.moveFile(
            FileUtils.getFile(xmlPath),
            foregroundFile,
        )
        val drawableName: String = org.apache.commons.io.FilenameUtils.getBaseName(xmlPath)
        val resPath: String = org.apache.commons.io.FilenameUtils.getFullPath(xmlPath)
        val document = DocumentHelper.createDocument()
        val root = document.addElement("adaptive-icon")
            .addAttribute("xmlns:android", "http://schemas.android.com/apk/res/android")
        root.addElement("background")
            .addAttribute("android:drawable", bgColor)
        root.addElement("foreground").addElement("inset")
            .addAttribute("android:inset", "33.33%")
            .addAttribute(
                "android:drawable",
                "@drawable/" + org.apache.commons.io.FilenameUtils.getBaseName(forgroundXml),
            )
//        root.addElement("monochrome").addElement("inset")
//            .addAttribute("android:inset", "32%")
//            .addAttribute("android:drawable", "@drawable/" + drawableName + "_monochrome")
        updateDocumentToFile(document, "$resPath$drawableName.xml")
    }
    private fun updateXmlPath(xmlPath: String, searchKey: String, attributeValue: String) {
        val xmlDocument = XmlUtil.getDocument(xmlPath)
        val keyWithoutNameSpace = searchKey.substring(searchKey.indexOf(":") + 1)
        if (xmlDocument.rootElement != null) {
            for (e in xmlDocument.rootElement.elements("path")) {
                val attr = e.attribute(keyWithoutNameSpace)
                if (attr != null) {
                    if (attr.value != "#00000000") {
                        attr.value = attributeValue
                    }
                }
            }
            XmlUtil.writeDocumentToFile(xmlDocument, xmlPath)
        }
    }
    @Throws(IOException::class)
    private fun updateDocumentToFile(outDocument: Document, outputConfigPath: String) {
        val fileWriter = FileWriter(outputConfigPath)
        val format = OutputFormat.createPrettyPrint()
        format.encoding = StandardCharsets.UTF_8.name()
        val writer = XMLWriter(fileWriter, format)
        writer.write(outDocument)
        writer.close()
    }
}
