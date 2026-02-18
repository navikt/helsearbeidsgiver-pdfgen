import io.ktor.client.HttpClient
import io.ktor.client.engine.apache5.Apache5
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertTrue

const val SYKMELDING_PDF_ROUTE = "/api/v1/genpdf/sykmelding/sykmelding"
const val SOKNAD_PDF_ROUTE = "/api/v1/genpdf/soknad/soknad"

fun lagPdfOgHentTekst(
    jsonNavn: String,
    pdfgenRoute: String,
): String {
    val jsonPath = "src/test/resources/$jsonNavn.json"
    val jsonData = File(jsonPath).readText()
    println("Kaller pdfgen på [$pdfgenRoute] med fil fra [$jsonPath]")
    val pdfBytes = hentPdf(pdfgenRoute, jsonData)
    pdfBytes.lagreTestPdf(jsonNavn)
    return pdfBytes.toText()
}

fun ByteArray.lagreTestPdf(
    navn: String,
    destinasjon: File = File("build/test-pdf"),
) {
    val testPdfDir = destinasjon.apply { mkdirs() }
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

        response.readRawBytes()
    }

fun String.skalInneholde(vararg forventetInnhold: String): List<DynamicTest> =
    skalInneholdeInternal(*forventetInnhold, skalAssertTrue = true)

fun String.skalIkkeInneholde(vararg ikkeForventetInnhold: String): List<DynamicTest> =
    skalInneholdeInternal(*ikkeForventetInnhold, skalAssertTrue = false)

private fun String.skalInneholdeInternal(
    vararg forventetInnhold: String,
    skalAssertTrue: Boolean,
): List<DynamicTest> =
    forventetInnhold.asList().map { forventent ->
        val displayName = if (skalAssertTrue) forventent else "Har ikke: $forventent"
        dynamicTest(displayName) {
            if (skalAssertTrue) {
                assertTrue(
                    this.contains(forventent),
                    "Forvent at PDF skal inneholde '$forventent'. PDF content: $this",
                )
            } else {
                assertFalse(
                    this.contains(forventent),
                    "Forvent at PDF IKKE skal inneholde '$forventent' men fant det likevel. PDF content: $this",
                )
            }
        }
    }
