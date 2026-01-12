import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.io.File
import kotlin.test.assertTrue

class PdfTest {
    @TestFactory
    fun `sykmelding PDF inneholder forventet tekst`(): List<DynamicTest?> {
        val jsonData = File("data/sykmelding/sykmelding.json").readText()
        val pdfBytes = makePdfRequest("/api/v1/genpdf/sykmelding/sykmelding", jsonData)
        File("response.pdf").writeBytes(pdfBytes)
        val pdfText = pdfBytes.toText()

        println("PDF text content (first 200 chars): ${pdfText.take(200)}...")

        val forvententInnhold =
            listOf(
                "Sykmelding",
                "Ola Nordmann",
                "12345678901",
                "c8cdcb93-75d3-4461-b09e-454a1ccd55a2",
                "Perioder i sykmeldingen",
            )
        return forvententInnhold.map { forventent ->
            dynamicTest(forventent) {
                assertTrue(
                    pdfText.contains(forventent),
                    "Forvent at PDF skal inneholde '$forventent' but det var ikke funnet. PDF content: $pdfText",
                )
            }
        }
    }

    @Test
    fun `test som skal feile`() {
        assertTrue(false, "Test som skal feile")
    }
}
