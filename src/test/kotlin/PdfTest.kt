import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertTrue

const val SYKMELDING_PDF_ROUTE = "/api/v1/genpdf/sykmelding/sykmelding"

private val PDF_TEKST: String =
    run {
        val jsonData = SYKMELDING_JSON
        val pdfBytes = hentPdf(SYKMELDING_PDF_ROUTE, jsonData)
        pdfBytes.lagreTestPdf("sykmelding")
        pdfBytes.toText()
    }

class PdfTest {
    private fun String.skalInneholde(vararg forventetInnhold: String): List<DynamicTest> =
        forventetInnhold.asList().map { forventent ->
            dynamicTest(forventent) {
                assertTrue(
                    this.contains(forventent),
                    "Forvent at PDF skal inneholde '$forventent' men det var ikke funnet. PDF content: $PDF_TEKST",
                )
            }
        }

    @TestFactory
    fun `sykmelding har involverte parter`(): List<DynamicTest> =
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
    fun `sykmelding har metadata`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Sykmelding",
            "Mottatt av Nav",
            "01.01.2023 01:23",
            "Sykmeldings-ID:",
            "sykmelding_id",
        )

    @TestFactory
    fun `sykmelding har perioder`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Perioder i sykmeldingen",
            "Sykefravær fra:",
            "2026-01-01",
            "2026-01-03",
            "2026-01-24",
            "Med reisetilskudd",
            "Manglende tilrettelegging på \narbeidsplassen",
            "aktivitetIkkeMulig_beskrivelse",
        )

    @TestFactory
    fun `sykmelding har egenmeldingsdager`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Egenmeldingsdager",
            "Oppgitt av ansatt selv ved bekreftelse av sykmelding.",
            "2026-01-01",
            "2026-01-02",
        )

    @TestFactory
    fun `sykmelding har oppfølging`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Oppfølging",
            "Prognose og hensyn etter sykefravær",
            "Arbeidsfør etter endt periode",
            "oppfoelging_beskrivHensynArbeidsplassen",
            "Tiltak som kan bedre ansattes arbeidsevne",
            "oppfoelging_tiltakArbeidsplassen",
            "Melding fra behandler til arbeidsgiver",
            "oppfoelging_meldingTilArbeidsgiver",
        )
}
