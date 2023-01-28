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
import org.dom4j.DocumentException

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
            try {
                val attrValue = if (mode == "dark") "#000" else "#fff"
                updateXmlPath(targetFile, "android:strokeColor", attrValue)
                updateXmlPath(targetFile, "android:fillColor", attrValue)
            } catch (e: DocumentException) {
                throw RuntimeException(e)
            }
        } else {
            println("Skipping file as its not svg " + svgSource.fileName)
        }
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
