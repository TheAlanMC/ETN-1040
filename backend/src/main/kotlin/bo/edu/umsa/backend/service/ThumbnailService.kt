package bo.edu.umsa.backend.service

import net.coobird.thumbnailator.Thumbnails
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Service
class ThumbnailService {

    fun createThumbnail(
        file: MultipartFile,
        minVal: Int
    ): ByteArray {
        println("Creating thumbnail for file ${file.originalFilename}")
        val outputStream = ByteArrayOutputStream()

        val originalImage = ImageIO.read(file.inputStream)
        val width = originalImage.width
        val height = originalImage.height

        val newWidth: Int
        val newHeight: Int

        if (width <= height) {
            newWidth = minVal
            newHeight = (height * (newWidth.toDouble() / width)).toInt()
        } else {
            newHeight = minVal
            newWidth = (width * (newHeight.toDouble() / height)).toInt()
        }

        Thumbnails.of(originalImage).size(newWidth, newHeight).outputFormat(file.originalFilename?.substringAfterLast(".")
            ?: "png").toOutputStream(outputStream)
        return outputStream.toByteArray()
    }
}