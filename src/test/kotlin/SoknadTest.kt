import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.Test
import kotlin.test.assertTrue

private val MINIMAL_SOKNAD_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "minimal_soknad", pdfgenRoute = SOKNAD_PDF_ROUTE)

private val FULLSTENDING_SOKNAD_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "fullstendig_soknad", pdfgenRoute = SOKNAD_PDF_ROUTE)

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class SoknadTest {
    @Test
    @Order(1)
    fun `kompiler PDF uten feil og lagre lokalt`() {
        assertTrue(MINIMAL_SOKNAD_PDF_TEKST.isNotEmpty())
        assertTrue(FULLSTENDING_SOKNAD_PDF_TEKST.isNotEmpty())
    }

    @Test
    fun `minimal søknad PDF har forventet innhold`() {
        """
        Søknad om sykepenger
        Mottatt av Nav 04.06.2025 14:50
        Søknads-ID: f0bb7352-485d-3974-a0c4-deb695813383
        Sykmeldings-ID: e40c0fd8-4d28-4960-bb83-e2f25313ac29
        Søknaden gjelder
        sykmeldtNavn
        Fødselsnummer
        fnr
        Arbeidsgiver
        arbeidsgiver_navn
        Organisasjonsnummer
        arbeidsgiver_orgnr
        Søknadsperioder
        100% sykmeldt 05.05.2025 – 06.05.2025 (2 dager)
        Sykmeldingsgrad: 100% Arbeidsuke: — Faktisk arbeidsgrad: — Arbeidet i perioden: —
        Arbeid gjenopptatt
        —
        Fraværsperioder
        Ingen fraværsperioder oppgitt.
        """.trimIndent().trim() shouldBe
            MINIMAL_SOKNAD_PDF_TEKST.also { println(it) }
    }

    @Test
    fun `fullstending søknad PDF har forventet innhold`() {
        """
        Søknad om sykepenger
        Mottatt av Nav 04.06.2025 14:50
        Søknads-ID: f0bb7352-485d-3974-a0c4-deb695813383
        Sykmeldings-ID: e40c0fd8-4d28-4960-bb83-e2f25313ac29
        Søknaden gjelder
        sykmeldtNavn
        Fødselsnummer
        fnr
        Arbeidsgiver
        arbeidsgiver_navn
        Organisasjonsnummer
        arbeidsgiver_orgnr
        Søknadsperioder
        100% sykmeldt 05.05.2025 – 05.05.2025 (1 dag)
        Sykmeldingsgrad: 100% Arbeidsuke: — Faktisk arbeidsgrad: — Arbeidet i perioden: —
        50% sykmeldt 06.05.2025 – 07.05.2025 (2 dager)
        Sykmeldingsgrad: 50% Arbeidsuke: 37.5 t Faktisk arbeidsgrad: 10 % Arbeidet i perioden: —
        Behandlingsdager 08.05.2025 – 10.05.2025 (3 dager)
        Sykmeldingsgrad: 0% Arbeidsuke: 37.5 t Faktisk arbeidsgrad: 10 % Arbeidet i perioden: 3.0 t
        Avventende sykmelding 11.05.2025 – 14.05.2025 (4 dager)
        Sykmeldingsgrad: 0% Arbeidsuke: — Faktisk arbeidsgrad: — Arbeidet i perioden: —
        Reisetilskudd 15.05.2025 – 19.05.2025 (5 dager)
        Sykmeldingsgrad: 0% Arbeidsuke: — Faktisk arbeidsgrad: — Arbeidet i perioden: —
        Arbeid gjenopptatt
        21.06.2025
        Fraværsperioder
        Ferie 06.05.2025 – 06.05.2025 (1 dag)
        Permisjon 07.05.2025 – 08.05.2025 (2 dager)
        Utenlandsopphold 09.05.2025 – 11.05.2025 (3 dager)
        """.trimIndent().trim() shouldBe
            FULLSTENDING_SOKNAD_PDF_TEKST.also { println(it) }
    }
}
