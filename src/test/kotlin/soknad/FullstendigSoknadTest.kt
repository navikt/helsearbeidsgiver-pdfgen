package soknad

import SOKNAD_PDF_ROUTE
import SYKMELDING_PDF_ROUTE
import lagPdfOgHentTekst
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestMethodOrder
import skalInneholde
import kotlin.test.Test
import kotlin.test.assertTrue

private val PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "fullstendig_soknad", pdfgenRoute = SOKNAD_PDF_ROUTE)

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class FullstendigSoknadTest {
    @Test
    @Order(1)
    fun `kompiler søknad PDF uten feil og lagre lokalt`() {
        assertTrue(PDF_TEKST.isNotEmpty())
    }

    @TestFactory
    fun `søknad PDF har involverte parter`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Sykmeldingen gjelder",
            "—",
            "Fødselsnummer",
            "fnr",
            "Arbeidsgiver",
            "arbeidsgiver_navn",
            "Organisasjonsnummer",
            "arbeidsgiver_orgnr",
        )
}
