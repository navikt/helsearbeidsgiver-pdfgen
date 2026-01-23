import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertTrue

const val SYKMELDING_PDF_ROUTE = "/api/v1/genpdf/sykmelding/sykmelding"

class PdfTest {
    companion object {
        private val pdfText: String by lazy {
            val jsonData = SYKMELDING_JSON
            val pdfBytes = hentPdf(SYKMELDING_PDF_ROUTE, jsonData)
            pdfBytes.lagreTestPdf("sykmelding")
            pdfBytes.toText()
        }
    }

    private fun String.skalInneholde(vararg forventetInnhold: String): List<DynamicTest> =
        forventetInnhold.asList().map { forventent ->
            dynamicTest(forventent) {
                assertTrue(
                    this.contains(forventent),
                    "Forvent at PDF skal inneholde '$forventent' men det var ikke funnet. PDF content: $pdfText",
                )
            }
        }

    @TestFactory
    fun `sykmelding har involverte parter`(): List<DynamicTest> =
        pdfText.skalInneholde(
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
        pdfText.skalInneholde(
            "Sykmelding",
            "Mottatt av Nav",
            "01.01.2023 01:23",
            "Sykmeldings-ID:",
            "sykmelding_id",
        )

    @TestFactory
    fun `sykmelding har perioder`(): List<DynamicTest> =
        pdfText.skalInneholde(
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
        pdfText.skalInneholde(
            "Egenmeldingsdager",
            "Oppgitt av ansatt selv ved bekreftelse av sykmelding.",
            "2026-01-01",
            "2026-01-02",
        )

    @TestFactory
    fun `sykmelding har oppfølging`(): List<DynamicTest> =
        pdfText.skalInneholde(
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
