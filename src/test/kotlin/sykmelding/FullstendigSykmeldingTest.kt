package sykmelding

import SYKMELDING_PDF_ROUTE
import lagPdfOgHentTekst
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestMethodOrder
import skalIkkeInneholde
import skalInneholde
import kotlin.test.Test
import kotlin.test.assertTrue

private val PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "fullstendig_sykmelding", pdfgenRoute = SYKMELDING_PDF_ROUTE)

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class FullstendigSykmeldingTest {
    @Test
    @Order(1)
    fun `kompiler sykmelding PDF uten feil og lagre lokalt`() {
        assertTrue(PDF_TEKST.isNotEmpty())
    }

    @TestFactory
    fun `sykmelding PDF har involverte parter`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Sykmeldingen gjelder",
            "sykmeldt_navn",
            "Fødselsnummer",
            "sykmeldt_fnr",
            "Arbeidsgiver",
            "arbeidsgiver_navn",
            "Organisasjonsnummer",
            "arbeidsgiver_orgnr",
            "Sykmelding skrevet av",
            "behandler_navn",
            "Telefon",
            "behandler_tlf",
        )

    @TestFactory
    fun `sykmelding PDF har metadata`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Sykmelding",
            "Mottatt av Nav",
            "01.01.2023 01:23",
            "Sykmeldings-ID:",
            "sykmelding_id",
        )

    @TestFactory
    fun `sykmelding PDF har perioder`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Perioder i sykmeldingen",
            "Sykefravær fra: 22.05.2025",
            "100% sykmeldt 24.05.2025 – 24.05.2025 (1 dag)",
            "100% sykmeldt 25.05.2025 – 26.05.2025 (2 dager) Manglende tilrettelegging på arbeidsplassen",
            "50% sykmeldt 28.05.2025 – 30.05.2025 (3 dager) Med reisetilskudd",
            "50% sykmeldt 01.06.2025 – 04.06.2025 (4 dager) Uten reisetilskudd",
            "Avventende sykmelding 06.06.2025 – 10.06.2025 (5 dager)",
            "Behandlingsdager 12.06.2025 – 17.06.2025 (6 dager) 1 behandlingsdag(er)",
            "Reisetilskudd 19.06.2025 – 25.06.2025 (7 dager)",
        )

    @TestFactory
    fun `sykmelding PDF har egenmeldingsdager`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Egenmeldingsdager",
            "Oppgitt av ansatt selv ved bekreftelse av sykmelding.",
            "21.05.2025 – 21.05.2025 (1 dag)",
            "22.05.2025 – 23.05.2025 (2 dager)",
        ) +
            PDF_TEKST.skalIkkeInneholde(
                "Ingen egenmeldingsdager oppgitt.",
            )

    @TestFactory
    fun `sykmelding PDF har oppfølging`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Oppfølging",
            "Prognose og hensyn etter sykefravær",
            "Arbeidsfør etter endt periode",
            "oppfoelging_beskrivHensynArbeidsplassen",
            "Tiltak som kan bedre ansattes arbeidsevne",
            "oppfoelging_tiltakArbeidsplassen",
            "Melding fra behandler til arbeidsgiver",
            "oppfoelging_meldingTilArbeidsgiver",
            "Forhold på arbeidsplassen vanskeliggjør arbeidsrelatert aktivitet",
            "aktivitet_aktivitetIkkeMulig_beskrivelse",
            "Innspill til arbeidsgiver om tilrettelegging",
            "aktivitet_avventendeSykmelding",
        ) +
            PDF_TEKST.skalIkkeInneholde(
                "Ingen oppfølging oppgitt.",
            )
}
