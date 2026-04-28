import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.Test
import kotlin.test.assertTrue

private val MINIMAL_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "minimal_kronisk_soknad", pdfgenRoute = KRONISK_SOKNAD_PDF_ROUTE)

private val FULLSTENDING_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "fullstendig_kronisk_soknad", pdfgenRoute = KRONISK_SOKNAD_PDF_ROUTE)

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class KroniskSoknadTest {
    @Test
    @Order(1)
    fun `kompiler PDF uten feil og lagre lokalt`() {
        assertTrue(MINIMAL_PDF_TEKST.isNotEmpty())
        assertTrue(FULLSTENDING_PDF_TEKST.isNotEmpty())
    }

    @Test
    fun `minimal kronisk søknad PDF har forventet innhold`() {
        MINIMAL_PDF_TEKST.also { println(it) } shouldBe
            """
            Søknad om fritak fra arbeidsgiverperiode - kronisk syk
            Mottatt 31.03.2026 14:16
            Søknaden gjelder
            —
            Fødselsnummer
            02127437127
            Arbeidsgiver
            —
            Organisasjonsnummer
            815493000
            Vedlegg til søknad
            Dokumenter er vedlagt
            Historisk fravær
            Det finnes ikke historisk fravær på grunn av nyansettelse, lengre permisjon eller annet.
            Innrapporert av
            —
            """.trimIndent().trim()
    }

    @Test
    fun `fullstending kronisk søknad PDF har forventet innhold`() {
        FULLSTENDING_PDF_TEKST.also { println(it) } shouldBe
            """
            Søknad om fritak fra arbeidsgiverperiode - kronisk syk
            Mottatt 31.03.2026 14:16 søknadsID: 123456
            Søknaden gjelder
            Opplyst Balltre
            Fødselsnummer
            02127437127
            Arbeidsgiver
            Intrikat Justerbar Tiger AS
            Organisasjonsnummer
            815493000
            Vedlegg til søknad
            Ingen dokumenter vedlagt
            Historisk fravær
            2024: 18 dager
            2025: 24 dager
            2026: 8 dager
            Antall fraværsperioder siste 2 år: 26
            Innrapporert av
            Bevisst Bøk
            """.trimIndent().trim()
    }
}
