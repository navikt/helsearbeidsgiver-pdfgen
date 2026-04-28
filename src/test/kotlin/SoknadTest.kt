import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.Test
import kotlin.test.assertTrue

private val MINIMAL_SOKNAD_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "minimal_soknad", pdfgenRoute = SOKNAD_PDF_ROUTE)

private val FULLSTENDIG_SOKNAD_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "fullstendig_soknad", pdfgenRoute = SOKNAD_PDF_ROUTE)

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class SoknadTest {
    @Test
    @Order(1)
    fun `kompiler PDF uten feil og lagre lokalt`() {
        assertTrue(MINIMAL_SOKNAD_PDF_TEKST.isNotEmpty())
        assertTrue(FULLSTENDIG_SOKNAD_PDF_TEKST.isNotEmpty())
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
        100% sykmeldt 05.05.2025 – 03.06.2025 (30 dager)
        Sykmeldingsgrad: 100% Arbeidsuke: — Faktisk arbeidsgrad: — Arbeidet i perioden: —
        Arbeid gjenopptatt
        —
        Fraværsperioder
        Ingen fraværsperioder oppgitt.
        """.trimIndent().trim() shouldBe
            MINIMAL_SOKNAD_PDF_TEKST.also { println(it) }
    }

    @Test
    fun `fullstendig søknad PDF har forventet innhold`() {
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
        100% sykmeldt 05.05.2025 – 03.06.2025 (30 dager)
        Sykmeldingsgrad: 100% Arbeidsuke: — Faktisk arbeidsgrad: — Arbeidet i perioden: —
        50% sykmeldt 01.04.2025 – 06.04.2025 (6 dager)
        Sykmeldingsgrad: 50% Arbeidsuke: 37.5 t Faktisk arbeidsgrad: 10 % Arbeidet i perioden: —
        Behandlingsdager 01.04.2025 – 06.04.2025 (6 dager)
        Sykmeldingsgrad: 0% Arbeidsuke: 37.5 t Faktisk arbeidsgrad: 10 % Arbeidet i perioden: 3.0 t
        Avventende sykmelding 01.04.2025 – 06.04.2025 (6 dager)
        Sykmeldingsgrad: 0% Arbeidsuke: — Faktisk arbeidsgrad: — Arbeidet i perioden: —
        Reisetilskudd 01.04.2025 – 06.04.2025 (6 dager)
        Sykmeldingsgrad: 0% Arbeidsuke: — Faktisk arbeidsgrad: — Arbeidet i perioden: —
        Arbeid gjenopptatt
        21.06.2025
        Fraværsperioder
        Ferie 01.04.2025 – 06.04.2025 (6 dager)
        Permisjon 01.04.2025 – 06.04.2025 (6 dager)
        Utenlandsopphold 01.04.2025 – 06.04.2025 (6 dager)
        """.trimIndent().trim() shouldBe
            FULLSTENDIG_SOKNAD_PDF_TEKST.also { println(it) }
    }
}
