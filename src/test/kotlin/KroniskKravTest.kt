import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.Test
import kotlin.test.assertTrue

private val MINIMAL_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "minimal_kronisk_krav", pdfgenRoute = KRONISK_KRAV_PDF_ROUTE)

private val FULLSTENDING_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "fullstendig_kronisk_krav", pdfgenRoute = KRONISK_KRAV_PDF_ROUTE)

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class KroniskKravTest {
    @Test
    @Order(1)
    fun `kompiler PDF uten feil og lagre lokalt`() {
        assertTrue(MINIMAL_PDF_TEKST.isNotEmpty())
        assertTrue(FULLSTENDING_PDF_TEKST.isNotEmpty())
    }

    @Test
    fun `minimal kronisk krav PDF har forventet innhold`() {
        MINIMAL_PDF_TEKST.also { println(it) } shouldBe
            """
            Krav om refusjon for arbeidsgiverperiode - Kronisk syk
            Sendt 31.03.2026 14:16
            Kravet gjelder
            —
            Fødselsnummer
            02127437127
            Arbeidsgiver
            Organisasjonsnummer
            815493000
            Fraværsperioder
            03.11.2025 – 09.11.2025 (7 dager) Antall dager fravær i perioden: 4
            Sykmeldingsgrad: 100 % Beregnet månedsinntekt: 44000 Dagsats: 2030 kr Beløp periode: 8120 kr
            Innrapporert av
            —
            """.trimIndent().trim()
    }

    @Test
    fun `fullstending kronisk krav PDF har forventet innhold`() {
        FULLSTENDING_PDF_TEKST.also { println(it) } shouldBe
            """
            Krav om refusjon for arbeidsgiverperiode - Kronisk syk
            Sendt 31.03.2026 14:16 ID: 123456
            Kravet gjelder
            Opplyst Balltre
            Fødselsnummer
            02127437127
            Arbeidsgiver
            Intrikat Justerbar Tiger AS
            Organisasjonsnummer
            815493000
            Fraværsperioder
            03.11.2025 – 09.11.2025 (7 dager) Antall dager fravær i perioden: 4
            Sykmeldingsgrad: 100 % Beregnet månedsinntekt: 44000 Dagsats: 2030 kr Beløp periode: 8120 kr
            Innrapporert av
            Bevisst Bøk
            """.trimIndent().trim()
    }
}
