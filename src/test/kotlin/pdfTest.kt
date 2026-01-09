import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

@Testcontainers
class PdfTest {

    @Container
    val pdfGenContainer: GenericContainer<*> = GenericContainer("flex-sykepengesoknad-pdfgen:latest")
        .withExposedPorts(8080)
        .withFileSystemBind(
            Paths.get("templates").toAbsolutePath().toString(),
            "/app/templates"
        )
        .withFileSystemBind(
            Paths.get("fonts").toAbsolutePath().toString(),
            "/app/fonts"
        )
        .withFileSystemBind(
            Paths.get("resources").toAbsolutePath().toString(),
            "/app/resources"
        )

    @Test
    fun `test PDF generation with sykmelding data`() {
        // Read the JSON data
        val jsonData = File("data/sykmelding/sykmelding.json").readText()

        // Get the mapped port
        val port = pdfGenContainer.getMappedPort(8080)
        val baseUrl = "http://localhost:$port"

        // Create HTTP client
        val client = OkHttpClient()

        // Create request body
        val mediaType = "application/json".toMediaType()
        val body = jsonData.toRequestBody(mediaType)

        // Build the request
        val request = Request.Builder()
            .url("$baseUrl/api/v1/genpdf/sykmelding/sykmelding")
            .post(body)
            .build()

        // Execute the request
        val response = client.newCall(request).execute()

        // Assert 200 OK
        assertEquals(200, response.code, "Expected HTTP 200 OK")

        // Save the response as PDF
        val pdfBytes = response.body?.bytes()
        if (pdfBytes != null) {
            File("response.pdf").writeBytes(pdfBytes)
            println("PDF saved to response.pdf")
        }

        response.close()
    }
}
