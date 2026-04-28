import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Order
import kotlin.test.Test
import kotlin.test.assertTrue

private val MINIMAL_SYKMELDING_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "minimal_sykmelding", pdfgenRoute = SYKMELDING_PDF_ROUTE)

private val FULLSTENDIG_SYKMELDING_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "fullstendig_sykmelding", pdfgenRoute = SYKMELDING_PDF_ROUTE)

class SykmeldingTest {
    @Test
    @Order(1)
    fun `kompiler minimal sykmelding uten feil og lagre lokalt`() {
        assertTrue(MINIMAL_SYKMELDING_PDF_TEKST.isNotEmpty())
    }

    @Test
    fun `minimal sykmelding PDF har forventet innhold`() {
        """
        Sykmelding
        Mottatt av Nav 01.01.2023 01:23 Sykmeldings-ID: sykmelding_id
        Sykmeldingen gjelder
        sykmeldt_navn
        Fødselsnummer
        sykmeldt_fnr
        Arbeidsgiver
        arbeidsgiver_navn
        Organisasjonsnummer
        arbeidsgiver_orgnr
        Perioder i sykmeldingen
        Syketilfelle fra: —
        100% sykmeldt 23.05.2025 – 24.05.2025 (2 dager)
        Egenmeldingsdager
        Ingen egenmeldingsdager oppgitt.
        Oppfølging
        Ingen oppfølging oppgitt.
        Sykmelding skrevet av
        —
        Telefon
        —
        Kontakt med pasient
        01.01.2025 01:23
        """.trimIndent().trim() shouldBe
            MINIMAL_SYKMELDING_PDF_TEKST.also { println(it) }
    }

    @Test
    fun `fullstendig sykmelding PDF har forventet innhold`() {
        """
        Sykmelding
        Mottatt av Nav 01.01.2023 01:23 Sykmeldings-ID: sykmelding_id
        Sykmeldingen gjelder
        sykmeldt_navn 
        som_er_veldig_langt 
        for_å_teste_formattering
        Fødselsnummer
        sykmeldt_fnr
        Arbeidsgiver
        arbeidsgiver_navn 
        som_også_er_veldig_langt_AS
        Organisasjonsnummer
        arbeidsgiver_orgnr
        Perioder i sykmeldingen
        Syketilfelle fra: 22.05.2025
        100% sykmeldt 24.05.2025 – 24.05.2025 (1 dag)
        100% sykmeldt 25.05.2025 – 26.05.2025 (2 dager) Manglende tilrettelegging på arbeidsplassen
        50% sykmeldt 28.05.2025 – 30.05.2025 (3 dager) Med reisetilskudd
        50% sykmeldt 01.06.2025 – 04.06.2025 (4 dager) Uten reisetilskudd
        Avventende sykmelding 06.06.2025 – 10.06.2025 (5 dager)
        Behandlingsdager 12.06.2025 – 17.06.2025 (6 dager) 1 behandlingsdager
        Reisetilskudd 19.06.2025 – 25.06.2025 (7 dager)
        Egenmeldingsdager
        Oppgitt av ansatt selv ved bekreftelse av sykmelding.
        21.05.2025 – 21.05.2025 (1 dag)
        22.05.2025 – 23.05.2025 (2 dager)
        Oppfølging
        Prognose og hensyn etter sykefravær
        • Arbeidsfør etter endt periode
        oppfoelging_beskrivHensynArbeidsplassen
        Tiltak som kan bedre ansattes arbeidsevne
        oppfoelging_tiltakArbeidsplassen
        Melding fra behandler til arbeidsgiver
        oppfoelging_meldingTilArbeidsgiver
        Forhold på arbeidsplassen vanskeliggjør arbeidsrelatert aktivitet
        aktivitet_aktivitetIkkeMulig_beskrivelse
        Innspill til arbeidsgiver om tilrettelegging
        aktivitet_avventendeSykmelding
        Sykmelding skrevet av
        behandler_navn
        Telefon
        behandler_tlf
        Kontakt med pasient
        01.01.2025 01:23
        """.trimIndent().trim() shouldBe
            FULLSTENDIG_SYKMELDING_PDF_TEKST.also { println(it) }
    }
}
