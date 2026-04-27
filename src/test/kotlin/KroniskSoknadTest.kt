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
            "Søknad om fritak fra arbeidsgiverperiode - kronisk \n" +
            "syk\n" +
            "Sendt 31.03.2026 14:16\n" +
            "Søknaden gjelder\n" +
            "—\n" +
            "Fødselsnummer\n" +
            "02127437127\n" +
            "Arbeidsgiver\n" +
            "Organisasjonsnummer\n" +
            "815493000\n" +
            "Vedlegg til søknad\n" +
            "Dokumenter er vedlagt\n" +
            "Historisk fravær\n" +
            "Det finnes ikke historisk fravær på grunn av nyansettelse, lengre permisjon eller annet.\n" +
            "Innrapporert av\n" +
            "—"
    }

    @Test
    fun `fullstending kronisk søknad PDF har forventet innhold`() {
        FULLSTENDING_PDF_TEKST.also { println(it) } shouldBe
            "Søknad om fritak fra arbeidsgiverperiode - kronisk \n" +
            "syk\n" +
            "Sendt 31.03.2026 14:16 søknadsID: 123456\n" +
            "Søknaden gjelder\n" +
            "Opplyst Balltre\n" +
            "Fødselsnummer\n" +
            "02127437127\n" +
            "Arbeidsgiver\n" +
            "Intrikat Justerbar Tiger AS\n" +
            "Organisasjonsnummer\n" +
            "815493000\n" +
            "Vedlegg til søknad\n" +
            "Ingen dokumenter vedlagt\n" +
            "Historisk fravær\n" +
            "2024: 18 dager\n" +
            "2025: 24 dager\n" +
            "2026: 8 dager\n" +
            "Antall fraværsperioder siste 2 år: 26\n" +
            "Innrapporert av\n" +
            "Bevisst Bøk"
    }
}
