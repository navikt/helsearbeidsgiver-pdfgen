import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.Test
import kotlin.test.assertTrue

private val FULL_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "refusjon_full", pdfgenRoute = REFUSJON_PDF_ROUTE)
        .utenMellomromPaaLinjeslutt()

private val KOMPLEKS_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "refusjon_kompleks", pdfgenRoute = REFUSJON_PDF_ROUTE)
        .utenMellomromPaaLinjeslutt()

private val IKKE_RETT_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "refusjon_ikke_rett", pdfgenRoute = REFUSJON_PDF_ROUTE)
        .utenMellomromPaaLinjeslutt()

private val UTEN_INNTEKTSMELDING_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "refusjon_uten_inntektsmelding", pdfgenRoute = REFUSJON_PDF_ROUTE)
        .utenMellomromPaaLinjeslutt()

// PDF-tekst får mellomrom på slutten av linjer som brytes. Fjernes så forventet tekst kan skrives uten dem.
private fun String.utenMellomromPaaLinjeslutt(): String = lines().joinToString("\n") { it.trimEnd() }

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class RefusjonTest {
    @Test
    @Order(1)
    fun `kompiler PDF uten feil og lagre lokalt`() {
        assertTrue(FULL_PDF_TEKST.isNotEmpty())
        assertTrue(KOMPLEKS_PDF_TEKST.isNotEmpty())
        assertTrue(IKKE_RETT_PDF_TEKST.isNotEmpty())
        assertTrue(UTEN_INNTEKTSMELDING_PDF_TEKST.isNotEmpty())
    }

    @Test
    fun `full refusjon PDF har forventet innhold`() {
        """
        Informasjon om refusjon av sykepenger
        20.07.2026 12:34 Refusjons-ID: a1b2c3d4-0000-4000-8000-000000000001
        Refusjonen gjelder
        Johannes Hanssen
        Fødselsnummer
        02027410092
        Arbeidsgiver
        Nord-Aurdal kommune
        Organisasjonsnummer
        815493001
        Dette er en informasjonsmelding som inneholder opplysninger om utbetaling av sykepenger.
        Dine rettigheter som arbeidsgiver er avledet fra den sykmeldtes rettigheter til sykepenger.
        Oversikt over utbetalt refusjon
        Periode: 27.05.2026 – 26.06.2026
        Dagtype Oppgitt refusjon Forventet Utbetalt
        Sykepenger (100 %)
        11 dager
        40 000 kr/mnd
        1 846 kr/dag
        20 306 kr 20 306 kr
        Sum 20 306 kr 20 306 kr
        Dager igjen med rett til sykepenger: 101
        """.trimIndent().trim() shouldBe
            FULL_PDF_TEKST.also { println(it) }
    }

    @Test
    fun `kompleks refusjon med redusert utbetaling PDF har forventet innhold`() {
        """
        Informasjon om refusjon av sykepenger
        20.07.2026 12:34 Refusjons-ID: 6c078f5c-36ca-4c95-b377-1039f6617451
        Refusjonen gjelder
        Johannes Hanssen
        Fødselsnummer
        02027410092
        Arbeidsgiver
        Nord-Aurdal kommune
        Organisasjonsnummer
        815493001
        Dette er en informasjonsmelding som inneholder opplysninger om utbetaling av sykepenger.
        Dine rettigheter som arbeidsgiver er avledet fra den sykmeldtes rettigheter til sykepenger.
        Oversikt over utbetalt refusjon
        Periode: 27.05.2026 – 02.07.2026
        Dagtype Oppgitt refusjon Forventet Utbetalt
        Sykepenger (100 %)
        3 dager
        40 000 kr/mnd
        1 846 kr/dag
        5 538 kr 5 538 kr
        Sykepenger (100 %)
        3 dager
        80 000 kr/mnd
        3 692 kr/dag
        9 453 kr
        Begrenset til 6G
        9 453 kr
        Sykepenger (50 %)
        2 dager
        40 000 kr/mnd
        1 846 kr/dag
        1 846 kr 1 846 kr
        Sykepenger (50 %)
        2 dager
        80 000 kr/mnd
        3 692 kr/dag
        3 152 kr
        Begrenset til 6G
        3 152 kr
        Sykepenger (50 %)
        1 dag
        80 000 kr/mnd
        3 692 kr/dag
        1 576 kr
        Begrenset til 6G
        1 000 kr
        Ikke rett til sykepenger
        1 dag
        80 000 kr/mnd
        3 692 kr/dag
        1 576 kr
        Begrenset til 6G
        0 kr
        Sykepenger (50 %)
        1 dag
        0 kr/mnd
        0 kr/dag
        0 kr 0 kr
        Sykepenger (100 %)
        2 dager
        40 000 kr/mnd
        1 846 kr/dag
        3 692 kr 2 400 kr
        Sum 26 833 kr 23 389 kr
        Dager igjen med rett til sykepenger: 101
        Det er differanse i oppgitt refusjon og utbetalt refusjon
        Refusjonen følger den ansattes rett til sykepenger. Blir refusjonen redusert, kan ikke Nav oppgi grunnen til
        arbeidsgiveren, fordi Nav har taushetsplikt. Den ansatte får begrunnelsen i vedtaket sitt.
        Vanlige grunner er blant annet:
        Inntekten er fastsatt på en annen måte, for eksempel skjønnsfastsatt fordi den avviker mer enn 25 % fra a-
        ordningen.
        Den ansatte får en annen ytelse fra Nav for de samme dagene.
        """.trimIndent().trim() shouldBe
            KOMPLEKS_PDF_TEKST.also { println(it) }
    }

    @Test
    fun `ikke rett til sykepenger PDF har forventet innhold`() {
        """
        Informasjon om refusjon av sykepenger
        20.07.2026 12:34 Refusjons-ID: a1b2c3d4-0000-4000-8000-000000000003
        Refusjonen gjelder
        Johannes Hanssen
        Fødselsnummer
        02027410092
        Arbeidsgiver
        Nord-Aurdal kommune
        Organisasjonsnummer
        815493001
        Dette er en informasjonsmelding som inneholder opplysninger om utbetaling av sykepenger.
        Dine rettigheter som arbeidsgiver er avledet fra den sykmeldtes rettigheter til sykepenger.
        Oversikt over utbetalt refusjon
        Periode: 27.05.2026 – 19.06.2026
        Dagtype Oppgitt refusjon Forventet Utbetalt
        Ikke rett til sykepenger
        6 dager
        40 000 kr/mnd
        1 846 kr/dag
        11 076 kr 0 kr
        Sum 11 076 kr 0 kr
        Dager igjen med rett til sykepenger: 101
        """.trimIndent().trim() shouldBe
            IKKE_RETT_PDF_TEKST.also { println(it) }
    }

    @Test
    fun `refusjon uten inntektsmelding PDF har forventet innhold`() {
        """
        Informasjon om refusjon av sykepenger
        20.07.2026 12:34 Refusjons-ID: a1b2c3d4-0000-4000-8000-000000000004
        Refusjonen gjelder
        Johannes Hanssen
        Fødselsnummer
        02027410092
        Arbeidsgiver
        Nord-Aurdal kommune
        Organisasjonsnummer
        815493001
        Dette er en informasjonsmelding som inneholder opplysninger om utbetaling av sykepenger.
        Dine rettigheter som arbeidsgiver er avledet fra den sykmeldtes rettigheter til sykepenger.
        Oversikt over utbetalt refusjon
        Periode: 12.06.2026 – 26.06.2026
        Dagtype Oppgitt refusjon Forventet Utbetalt
        Sykepenger (100 %)
        6 dager
        1 500 kr/dag 9 000 kr 9 000 kr
        Sykepenger (60 %)
        5 dager
        1 500 kr/dag 4 500 kr 4 500 kr
        Sum 13 500 kr 13 500 kr
        Dager igjen med rett til sykepenger: 101
        """.trimIndent().trim() shouldBe
            UTEN_INNTEKTSMELDING_PDF_TEKST.also { println(it) }
    }
}
