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
        Refusjon av sykepenger
        20.07.2026 12:34 Refusjons-ID: a1b2c3d4-0000-4000-8000-000000000001
        Ansatt
        Ola Nordmann
        Fødselsnummer
        TestFnr
        Arbeidsgiver
        Fiktiv Bedrift AS
        Organisasjonsnummer
        TestOrgnr
        Gjeldende periode 27.05.2026 → 26.06.2026
        Saken til den sykmeldte er behandlet. Dette er en informasjonsmelding som inneholder opplysninger
        om utbetaling av sykepenger. Dine rettigheter som arbeidsgiver er avledet fra den sykmeldtes
        rettigheter til sykepenger.
        Oppgitt refusjonsbeløp pr mnd i inntektsmelding
        40 000 kr
        Detaljer for perioden
        11 dager sykepenger (sykdomsgrad: 100%) - 1 846 kr/dag
        Refusjon for perioden
        20 306 kr
        """.trimIndent().trim() shouldBe
            FULL_PDF_TEKST.also { println(it) }
    }

    @Test
    fun `kompleks refusjon med redusert utbetaling PDF har forventet innhold`() {
        """
        Refusjon av sykepenger
        20.07.2026 12:34 Refusjons-ID: 6c078f5c-36ca-4c95-b377-1039f6617451
        Ansatt
        Ola Nordmann
        Fødselsnummer
        TestFnr
        Arbeidsgiver
        Fiktiv Bedrift AS
        Organisasjonsnummer
        TestOrgnr
        Gjeldende periode 27.05.2026 → 02.07.2026
        Saken til den sykmeldte er behandlet. Dette er en informasjonsmelding som inneholder opplysninger
        om utbetaling av sykepenger. Dine rettigheter som arbeidsgiver er avledet fra den sykmeldtes
        rettigheter til sykepenger.
        Oppgitt refusjonsbeløp pr mnd i inntektsmelding
        40 000 kr
        80 000 kr (Fom. 01.06.2026)
        40 000 kr (Fom. 08.06.2026)
        80 000 kr (Fom. 10.06.2026)
        0 kr (Fom. 24.06.2026)
        40 000 kr (Fom. 29.06.2026)
        Detaljer for perioden
        3 dager sykepenger (sykdomsgrad: 100%) - 1 846 kr/dag
        3 dager sykepenger (sykdomsgrad: 100%) - 3 151 kr/dag
        2 dager sykepenger (sykdomsgrad: 50%) - 923 kr/dag
        2 dager sykepenger (sykdomsgrad: 50%) - 1 576 kr/dag
        1 dag sykepenger (sykdomsgrad: 50%) - 1 000 kr/dag
        1 dag Ikke rett til sykepenger - 0 kr/dag
        1 dag sykepenger (sykdomsgrad: 50%) - 0 kr/dag
        2 dager sykepenger (sykdomsgrad: 100%) - 1 200 kr/dag
        Refusjon for perioden
        23 389 kr
        Mulige grunner til at beløp er annet enn forventet
        Dersom refusjonsbeløpet er lavere enn forventet, kan det skyldes ulike årsaker. Nav oppgir ikke
        den konkrete årsaken til redusert refusjon. Vanlige grunner til at en arbeidsgivers refusjon
        reduseres inkluderer, men er ikke begrenset til:
        6G flere AG
        Annen inntekt lagt til grunn pga. 25 % avvik
        Saksbehandler har overstyrt refusjonskravet
        etc...
        Den nærmere begrunnelsen for reduksjon av refusjon i din sak er gitt i vedtaket til den sykmeldte.
        """.trimIndent().trim() shouldBe
            KOMPLEKS_PDF_TEKST.also { println(it) }
    }

    @Test
    fun `ikke rett til sykepenger PDF har forventet innhold`() {
        """
        Refusjon av sykepenger
        20.07.2026 12:34 Refusjons-ID: a1b2c3d4-0000-4000-8000-000000000003
        Ansatt
        Ola Nordmann
        Fødselsnummer
        TestFnr
        Arbeidsgiver
        Fiktiv Bedrift AS
        Organisasjonsnummer
        TestOrgnr
        Gjeldende periode 27.05.2026 → 19.06.2026
        Saken til den sykmeldte er behandlet. Dette er en informasjonsmelding som inneholder opplysninger
        om utbetaling av sykepenger. Dine rettigheter som arbeidsgiver er avledet fra den sykmeldtes
        rettigheter til sykepenger.
        Oppgitt refusjonsbeløp pr mnd i inntektsmelding
        40 000 kr
        Detaljer for perioden
        6 dager Ikke rett til sykepenger - 0 kr/dag
        Refusjon for perioden
        0 kr
        """.trimIndent().trim() shouldBe
            IKKE_RETT_PDF_TEKST.also { println(it) }
    }

    @Test
    fun `refusjon uten inntektsmelding PDF har forventet innhold`() {
        """
        Refusjon av sykepenger
        20.07.2026 12:34 Refusjons-ID: a1b2c3d4-0000-4000-8000-000000000004
        Ansatt
        Ola Nordmann
        Fødselsnummer
        TestFnr
        Arbeidsgiver
        Fiktiv Bedrift AS
        Organisasjonsnummer
        TestOrgnr
        Gjeldende periode 12.06.2026 → 26.06.2026
        Saken til den sykmeldte er behandlet. Dette er en informasjonsmelding som inneholder opplysninger
        om utbetaling av sykepenger. Dine rettigheter som arbeidsgiver er avledet fra den sykmeldtes
        rettigheter til sykepenger.
        Oppgitt refusjonsbeløp pr mnd i inntektsmelding
        - kr
        Detaljer for perioden
        6 dager sykepenger (sykdomsgrad: 100%) - 1 500 kr/dag
        5 dager sykepenger (sykdomsgrad: 60%) - 900 kr/dag
        Refusjon for perioden
        13 500 kr
        """.trimIndent().trim() shouldBe
            UTEN_INNTEKTSMELDING_PDF_TEKST.also { println(it) }
    }
}
