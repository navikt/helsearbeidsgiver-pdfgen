import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.Test
import kotlin.test.assertTrue

private val FULLSTENDIG_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "refusjon_fullstendig", pdfgenRoute = REFUSJON_PDF_ROUTE)
        .utenMellomromPaaLinjeslutt()

private val MINIMAL_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "refusjon_minimal", pdfgenRoute = REFUSJON_PDF_ROUTE)
        .utenMellomromPaaLinjeslutt()

// PDF-tekst får mellomrom på slutten av linjer som brytes. Fjernes så forventet tekst kan skrives uten dem.
private fun String.utenMellomromPaaLinjeslutt(): String = lines().joinToString("\n") { it.trimEnd() }

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class RefusjonTest {
    @Test
    @Order(1)
    fun `kompiler PDF uten feil og lagre lokalt`() {
        assertTrue(FULLSTENDIG_PDF_TEKST.isNotEmpty())
        assertTrue(MINIMAL_PDF_TEKST.isNotEmpty())
    }

    // Endringer i refusjonskrav, dager med og uten rett til sykepenger, entall og flertall, og varsel om lavere beløp
    @Test
    fun `fullstendig refusjon PDF har forventet innhold`() {
        """
        Refusjon av sykepenger
        20.07.2026 12:34 Refusjons-ID: 6c078f5c-36ca-4c95-b377-1039f6617451
        Refusjon gjelder
        Ola Nordmann
        Fødselsnummer
        TestFnr
        Arbeidsgiver
        Fiktiv Bedrift AS
        Organisasjonsnummer
        TestOrgnr
        Søknad om sykepenger med refusjon er behandlet
        Nav har behandlet sykepengesaken til den ansatte. Beløpet avhenger av hvor mye sykepenger den
        ansatte har rett til, og utbetales til virksomhetens kontonummer.
        Gjeldende periode
        27.05.2026 → 02.07.2026
        Refusjon
        23 389 kr
        Grunnlag for refusjon
        Oppgitt refusjonsbeløp i inntektsmelding
        40 000 kr/måned
        80 000 kr/måned (Fra og med: 01.06.2026)
        40 000 kr/måned (Fra og med: 08.06.2026)
        80 000 kr/måned (Fra og med: 10.06.2026)
        0 kr/måned (Fra og med: 24.06.2026)
        40 000 kr/måned (Fra og med: 29.06.2026)
        Detaljer for perioden
        Dager Type Sykdomsgrad (etter søknad) Refusjon per dag
        3 dager Rett til sykepenger 100% 1 846 kr
        3 dager Rett til sykepenger 100% 3 151 kr
        2 dager Rett til sykepenger 50% 923 kr
        2 dager Rett til sykepenger 50% 1 576 kr
        1 dag Rett til sykepenger 50% 1 000 kr
        1 dag Ikke rett til sykepenger – 0 kr
        1 dag Rett til sykepenger 50% 0 kr
        2 dager Rett til sykepenger 100% 1 200 kr
        Lørdag, søndag og arbeidsgiverperiode vises ikke i listen over dager. Dager med rett til sykepenger og dager uten rett til
        sykepenger vil vises. Sykdomsgrad er hva som faktisk ble benyttet av sykmeldingen basert på informasjon gitt i søknaden om
        sykepenger. Beløpet under refusjon per dag er gradert og avkortet til seks ganger grunnbeløpet.
        Mulige grunner til at beløpet er annet enn forventet
        Dersom refusjonsbeløpet er lavere enn forventet, kan det skyldes ulike årsaker. Nav oppgir ikke
        den konkrete årsaken til redusert refusjon. Vanlige grunner til reduksjon inkluderer, men er ikke
        begrenset til:
        6G flere AG
        Annen inntekt lagt til grunn pga. 25 % avvik
        Saksbehandler har overstyrt refusjonskravet
        Den nærmere begrunnelsen for reduksjon av refusjon i din sak er gitt i vedtaket til den
        sykmeldte.
        """.trimIndent().trim() shouldBe
            FULLSTENDIG_PDF_TEKST.also { println(it) }
    }

    // Uten refusjonskrav fra inntektsmelding og uten varsel
    @Test
    fun `minimal refusjon PDF har forventet innhold`() {
        """
        Refusjon av sykepenger
        20.07.2026 12:34 Refusjons-ID: a1b2c3d4-0000-4000-8000-000000000004
        Refusjon gjelder
        Ola Nordmann
        Fødselsnummer
        TestFnr
        Arbeidsgiver
        Fiktiv Bedrift AS
        Organisasjonsnummer
        TestOrgnr
        Søknad om sykepenger med refusjon er behandlet
        Nav har behandlet sykepengesaken til den ansatte. Beløpet avhenger av hvor mye sykepenger den
        ansatte har rett til, og utbetales til virksomhetens kontonummer.
        Gjeldende periode
        12.06.2026 → 26.06.2026
        Refusjon
        13 500 kr
        Grunnlag for refusjon
        Oppgitt refusjonsbeløp i inntektsmelding
        -
        Detaljer for perioden
        Dager Type Sykdomsgrad (etter søknad) Refusjon per dag
        6 dager Rett til sykepenger 100% 1 500 kr
        5 dager Rett til sykepenger 60% 900 kr
        Lørdag, søndag og arbeidsgiverperiode vises ikke i listen over dager. Dager med rett til sykepenger og dager uten rett til
        sykepenger vil vises. Sykdomsgrad er hva som faktisk ble benyttet av sykmeldingen basert på informasjon gitt i søknaden om
        sykepenger. Beløpet under refusjon per dag er gradert og avkortet til seks ganger grunnbeløpet.
        """.trimIndent().trim() shouldBe
            MINIMAL_PDF_TEKST.also { println(it) }
    }
}
