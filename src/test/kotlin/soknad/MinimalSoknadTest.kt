package soknad

import SOKNAD_PDF_ROUTE
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
    lagPdfOgHentTekst(jsonNavn = "minimal_soknad", pdfgenRoute = SOKNAD_PDF_ROUTE)

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MinimalSoknadTest {
    @Test
    @Order(1)
    fun `kompiler søknad PDF uten feil og lagre lokalt`() {
        assertTrue(PDF_TEKST.isNotEmpty())
    }

    @TestFactory
    fun `søknad PDF har involverte parter`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Søknaden gjelder",
            "sykmeldtNavn",
            "Fødselsnummer",
            "fnr",
            "Arbeidsgiver",
            "arbeidsgiver_navn",
            "Organisasjonsnummer",
            "arbeidsgiver_orgnr",
        )

    @TestFactory
    fun `søknad PDF har metadata`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Mottatt av Nav 04.06.2025 14:50",
            "Søknads-ID: f0bb7352-485d-3974-a0c4-deb695813383",
            "Sykmeldings-ID: e40c0fd8-4d28-4960-bb83-e2f25313ac29",
        )

    @TestFactory
    fun `søknad PDF har søknadsperioder`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Søknadsperioder",
            "100% sykmeldt 05.05.2025 – 03.06.2025 (30 dager)",
            "Sykmeldingsgrad: 100% Arbeidsuke: — Faktisk arbeidsgrad: — Arbeidet i perioden: —",
        )

    @TestFactory
    fun `søknad PDF har arbeid gjenopptatt`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Arbeid gjenopptatt",
            "—",
        )

    @TestFactory
    fun `søknad PDF har fraværsperioder`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Fraværsperioder",
            "Ingen fraværsperioder oppgitt.",
        )
}
