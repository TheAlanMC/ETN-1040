package bo.edu.umsa.backend.service

import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service

@Service
class AssetService(private val resourceLoader: ResourceLoader) {

    fun readPhotoToByteArray(photoPath: String): ByteArray {
        val resource = resourceLoader.getResource("classpath:static/$photoPath")
        val file = resource.file
        return file.readBytes()
    }
}