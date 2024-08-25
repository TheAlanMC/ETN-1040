package bo.edu.umsa.backend.service

import bo.edu.umsa.backend.dto.Margins
import bo.edu.umsa.backend.dto.ReportOptions
import bo.edu.umsa.backend.exception.EtnException
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.ConnectionPool
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.util.StreamUtils
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

@Service
class PdfTurtleService {

    @Value("\${pdf-turtle.url}")
    private lateinit var pdfTurtleUrl: String

    private val client = OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES)).build()

    private val objectMapper = ObjectMapper()

    fun generatePdfWithHtmlTemplate(
        htmlTemplateName: String,
        model: String,
        templateEngine: String = "golang"
    ): ByteArray {
        val htmlTemplate = readResourceAsString("report/$htmlTemplateName.html")

        val footerHtmlTemplate = readResourceAsString("report/footer.html")
        val headerHtmlTemplate = readResourceAsString("report/header.html")
        val options = ReportOptions(
            excludeBuiltStyles = true,
            landscape = true,
            Margins(top = 25, right = 20, bottom = 20, left = 20),
            pageFormat = "Letter",
        )

        val jsonRequest = generateJsonRequest(footerHtmlTemplate, headerHtmlTemplate, htmlTemplate, model, options, templateEngine)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonRequest.toRequestBody(mediaType)

        val request = Request.Builder().url("$pdfTurtleUrl/api/pdf/from/html-template/render").post(requestBody).build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw EtnException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating PDF", "Error al generar el PDF")
            }
            return response.body!!.bytes()
        } catch (e: Exception) {
            throw EtnException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating PDF", "Error al generar el PDF")
        }
    }

    private fun readResourceAsString(resourcePath: String): String {
        try {
            val resource = ClassPathResource(resourcePath)
            val inputStream = resource.inputStream
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            throw EtnException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading resource $resourcePath", "Error al leer el recurso $resourcePath")
        }
    }

    private fun generateJsonRequest(
        footerHtmlTemplate: String,
        headerHtmlTemplate: String,
        htmlTemplate: String,
        model: String,
        options: ReportOptions,
        templateEngine: String
    ): String {
        val requestMap = mapOf("footerHtmlTemplate" to footerHtmlTemplate, "headerHtmlTemplate" to headerHtmlTemplate, "htmlTemplate" to htmlTemplate, "model" to model, "options" to options, "templateEngine" to templateEngine)
        return objectMapper.writeValueAsString(requestMap)
    }
}