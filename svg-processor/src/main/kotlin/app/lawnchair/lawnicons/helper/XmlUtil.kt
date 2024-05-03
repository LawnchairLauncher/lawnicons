package app.lawnchair.lawnicons.helper

import java.io.File
import java.io.FileWriter
import java.nio.file.Path
import org.dom4j.Document
import org.dom4j.Element
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter

object XmlUtil {
    private val UTF_8 = Charsets.UTF_8.name()

    fun getElements(document: Document, path: String): List<Element> {
        return document.rootElement.elements(path)
    }

    fun getDocument(xmlPath: String): Document {
        return SAXReader().apply { encoding = UTF_8 }.read(xmlPath)
    }

    fun getFileWithExtension(target: Path, extension: String = "xml"): String {
        val svgFilePath = target.toFile().absolutePath
        val index = svgFilePath.lastIndexOf(".")
        return buildString {
            if (index != -1) {
                append(svgFilePath.substring(0, index))
            }
            append(".$extension")
        }
    }

    fun writeDocumentToFile(outDocument: Document, outputConfigPath: String) {
        File(outputConfigPath).parentFile.mkdirs()
        // Delete existing file If any
        File(outputConfigPath).delete()
        FileWriter(outputConfigPath).use { fw ->
            XMLWriter(fw, OutputFormat.createPrettyPrint()).apply {
                write(outDocument)
                close()
            }
        }
    }
}
