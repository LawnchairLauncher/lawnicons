package app.lawnchair.lawnicons.helper

import com.android.ide.common.vectordrawable.Svg2Vector
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.FileVisitOption
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.util.EnumSet
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.dom4j.Document
import org.dom4j.DocumentException
import org.dom4j.DocumentHelper

object SvgFilesProcessor {
    private lateinit var sourceSvgPath: Path
    private lateinit var destinationVectorPath: Path
    fun process(sourceDirectory: String, destDirectory: String) {
        this.sourceSvgPath = Paths.get(sourceDirectory)
        this.destinationVectorPath = Paths.get(destDirectory)
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
            Svg2Vector.parseSvgToXml(svgSource, fileOutputStream)
            val fg = "@color/primaryForeground"
            val bg = "@color/primaryBackground"
            try {
                updateXmlPath(targetFile, "android:strokeColor", fg)
                updateXmlPath(targetFile, "android:fillColor", fg)
                updateRootElement(targetFile, "android:tint", fg)
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
        val foregroundXml = xmlPath.replace(".xml", "_foreground.xml")
        val foregroundFile = FileUtils.getFile(foregroundXml)
        foregroundFile.delete()
        FileUtils.moveFile(
            FileUtils.getFile(xmlPath),
            foregroundFile,
        )
        val drawableName: String = FilenameUtils.getBaseName(xmlPath)
        val resPath: String = FilenameUtils.getFullPath(xmlPath)
        val document = DocumentHelper.createDocument()
        val root = document.addElement("adaptive-icon")
            .addAttribute("xmlns:android", "http://schemas.android.com/apk/res/android")
        root.addElement("background")
            .addAttribute("android:drawable", bgColor)
        root.addElement("foreground").addElement("inset")
            .addAttribute("android:inset", "32%")
            .addAttribute(
                "android:drawable",
                "@drawable/" + FilenameUtils.getBaseName(foregroundXml),
            )
        root.addElement("monochrome").addElement("inset")
            .addAttribute("android:inset", "28%")
            .addAttribute(
                "android:drawable",
                "@drawable/" + FilenameUtils.getBaseName(foregroundXml),
            )
        XmlUtil.writeDocumentToFile(document, "$resPath$drawableName.xml")
    }

    private fun updateRootElement(xmlPath: String, key: String, value: String) {
        val aDocument: Document = XmlUtil.getDocument(xmlPath)
        val keyWithoutNameSpace = key.substring(key.indexOf(":") + 1)
        val attr = aDocument.rootElement.attribute(keyWithoutNameSpace)
        if (attr != null) {
            if (attr.value != "#00000000") {
                attr.value = value
            }
        } else {
            aDocument.rootElement.addAttribute(key, value)
        }
        XmlUtil.writeDocumentToFile(aDocument, xmlPath)
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
}
