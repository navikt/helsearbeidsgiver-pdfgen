import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.Test
import kotlin.test.assertTrue

private val MINIMAL_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "minimal_gravid_soknad", pdfgenRoute = GRAVID_SOKNAD_PDF_ROUTE)

private val FULLSTENDING_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "fullstendig_gravid_soknad", pdfgenRoute = GRAVID_SOKNAD_PDF_ROUTE)

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class GravidSoknadTest {
    @Test
    @Order(1)
    fun `kompiler PDF uten feil og lagre lokalt`() {
        assertTrue(MINIMAL_PDF_TEKST.isNotEmpty())
        assertTrue(FULLSTENDING_PDF_TEKST.isNotEmpty())
    }

    @Test
    fun `minimal gravid søknad PDF har forventet innhold`() {
        MINIMAL_PDF_TEKST.also { println(it) } shouldBe
            """
            Søknad om fritak fra arbeidsgiverperiode - Gravid
            Sendt 31.03.2026 14:16
            Søknaden gjelder
            —
            Fødselsnummer
            02127437127
            Arbeidsgiver
            Organisasjonsnummer
            815493000
            Termindato
            —
            Arbeidssituasjon og miljø
            Har dere prøvd å tilrettelegge arbeidsdagen slik at den gravide kan jobbe til tross for helseplager?
            Nei
            Har dere forsøkt omplassering til annen jobb?
            —
            Vedlegg til søknad
            Ingen dokumenter vedlagt
            Innrapporert av
            —
            """.trimIndent().trim()
    }

    @Test
    fun `fullstending gravid søknad PDF har forventet innhold`() {
        FULLSTENDING_PDF_TEKST.also { println(it) } shouldBe
            """
            Søknad om fritak fra arbeidsgiverperiode - Gravid
            Sendt 31.03.2026 14:16 søknadsID: 123456
            Søknaden gjelder
            Opplyst Balltre
            Fødselsnummer
            02127437127
            Arbeidsgiver
            Intrikat Justerbar Tiger AS
            Organisasjonsnummer
            815493000
            Termindato
            24.06.2026
            Arbeidssituasjon og miljø
            Har dere prøvd å tilrettelegge arbeidsdagen slik at den gravide kan jobbe til tross for helseplager?
            Ja
            Hvilke tiltak har dere forsøkt eller vurdert for at den ansatte kan jobbe:
            • Hjemmekontor
            • Annet, gi en kort beskrivelse av hva dere har gjort:
            Her gir jeg en kort begrunnelse på tiltak vi har forsøkt.
            Har dere forsøkt omplassering til annen jobb?
            Omplassering er ikke mulig - Vi får ikke kontakt med den ansatte
            Vedlegg til søknad
            Ingen dokumenter vedlagt
            Innrapporert av
            Bevisst Bøk
            """.trimIndent().trim()
    }
}
