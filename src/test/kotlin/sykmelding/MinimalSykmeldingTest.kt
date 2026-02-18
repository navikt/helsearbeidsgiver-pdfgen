package sykmelding

import SYKMELDING_PDF_ROUTE
import lagPdfOgHentTekst
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestFactory
import skalIkkeInneholde
import skalInneholde
import kotlin.test.Test
import kotlin.test.assertTrue

private val PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "minimal_sykmelding", pdfgenRoute = SYKMELDING_PDF_ROUTE)

class MinimalSykmeldingTest {
    @Test
    @Order(1)
    fun `kompiler minimal sykmelding uten feil og lagre lokalt`() {
        assertTrue(PDF_TEKST.isNotEmpty())
    }

    @TestFactory
    fun `sykmelding PDF har ingen sykefravaerFom`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Perioder i sykmeldingen",
            "Syketilfelle fra: —",
        )

    @TestFactory
    fun `sykmelding PDF har ikke oppgitt egenmeldingsdager`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Egenmeldingsdager",
            "Ingen egenmeldingsdager oppgitt.",
        ) +
            PDF_TEKST.skalIkkeInneholde(
                "Oppgitt av ansatt selv ved bekreftelse av sykmelding.",
            )

    @TestFactory
    fun `sykmelding PDF har ikke oppgitt oppfølging`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Oppfølging",
            "Ingen oppfølging oppgitt.",
        ) +
            PDF_TEKST.skalIkkeInneholde(
                "Prognose og hensyn etter sykefravær",
                "Arbeidsfør etter endt periode",
                "Tiltak som kan bedre ansattes arbeidsevne",
                "Melding fra behandler til arbeidsgiver",
                "Forhold på arbeidsplassen vanskeliggjør arbeidsrelatert aktivitet",
                "Innspill til arbeidsgiver om tilrettelegging",
            )

    @TestFactory
    fun `sykmelding PDF har ikke behandler detaljer oppgitt`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Sykmelding skrevet av\n—",
            "Telefon\n—",
            "Kontakt med pasient\n01.01.2025 01:23",
        )
}
