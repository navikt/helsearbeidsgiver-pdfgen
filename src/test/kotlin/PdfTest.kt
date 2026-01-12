import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertTrue

const val SYKMELDING_PDF_ROUTE = "/api/v1/genpdf/sykmelding/sykmelding"

class PdfTest {
    @TestFactory
    fun `sykmelding PDF inneholder forventet tekst`(): List<DynamicTest?> {
        val jsonData = SYKMELDING_JSON
        val pdfBytes = hentPdf(SYKMELDING_PDF_ROUTE, jsonData)
        pdfBytes.lagreTestPdf("sykmelding")
        val pdfText = pdfBytes.toText()

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
                    "Forvent at PDF skal inneholde '$forventent' men det var ikke funnet. PDF content: $pdfText",
                )
            }
        }
    }
}
