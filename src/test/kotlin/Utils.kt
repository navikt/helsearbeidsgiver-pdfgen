import io.ktor.client.HttpClient
import io.ktor.client.engine.apache5.Apache5
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.readBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import java.io.File
import kotlin.test.assertTrue

val SYKMELDING_JSON = File("src/test/resources/standard_sykmelding.json").readText()

fun ByteArray.lagreTestPdf(navn: String) {
    val testPdfDir = File("build/test-pdf").apply { mkdirs() }
    val pdfFile = File(testPdfDir, "$navn.pdf")
    pdfFile.writeBytes(this)
    println("PDF lagret til: ${pdfFile.absolutePath}")
}

fun ByteArray.toText(): String {
    val pdfDocument = Loader.loadPDF(this)
    val textStripper = PDFTextStripper()
    val extractedText = textStripper.getText(pdfDocument)
    pdfDocument.close()
    return extractedText
}

fun hentPdf(
    route: String,
    jsonBody: String,
): ByteArray =
    runBlocking {
        val client =
            HttpClient(Apache5) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 30000
                    socketTimeoutMillis = 60000
                }
            }

        SharedTestContainer.container
        val url = "${SharedTestContainer.endepunkt}$route"

        val response =
            client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(jsonBody)
            }

        client.close()

        if (response.status != HttpStatusCode.OK) {
            throw RuntimeException("Expected HTTP 200 OK but got ${response.status}")
        }

        response.readBytes()
    }

fun String.skalInneholde(vararg forventetInnhold: String): List<DynamicTest> =
    forventetInnhold.asList().map { forventent ->
        dynamicTest(forventent) {
            assertTrue(
                this.contains(forventent),
                "Forvent at PDF skal inneholde '$forventent' men det var ikke funnet. PDF content: $this",
            )
        }
    }
