import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.Test
import kotlin.test.assertTrue

private val FULL_REFUSJON_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "refusjon_full", pdfgenRoute = REFUSJON_PDF_ROUTE)
        .utenMellomromPaaLinjeslutt()

private val KOMPLEKS_REFUSJON_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "refusjon_kompleks", pdfgenRoute = REFUSJON_PDF_ROUTE)
        .utenMellomromPaaLinjeslutt()

private val IKKE_RETT_REFUSJON_PDF_TEKST: String =
    lagPdfOgHentTekst(jsonNavn = "refusjon_ikke_rett", pdfgenRoute = REFUSJON_PDF_ROUTE)
        .utenMellomromPaaLinjeslutt()

// PDF-tekst får mellomrom på slutten av linjer som brytes. Fjernes så forventet tekst kan skrives uten dem.
private fun String.utenMellomromPaaLinjeslutt(): String = lines().joinToString("\n") { it.trimEnd() }

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class RefusjonTest {
    @Test
    @Order(1)
    fun `kompiler PDF uten feil og lagre lokalt`() {
        assertTrue(FULL_REFUSJON_PDF_TEKST.isNotEmpty())
        assertTrue(KOMPLEKS_REFUSJON_PDF_TEKST.isNotEmpty())
        assertTrue(IKKE_RETT_REFUSJON_PDF_TEKST.isNotEmpty())
    }

    @Test
    fun `full refusjon PDF har forventet innhold`() {
        """
        Informasjon om refusjon av sykepenger
        20.07.2026 12:34 Refusjons-ID: a1b2c3d4-0000-4000-8000-000000000001
        Ansatt
        Johannes Hanssen
        Fødselsnummer
        02127437127
        Arbeidsgiver
        Nord-Aurdal kommune
        Organisasjonsnummer
        815493000
        Dette er en informasjonsmelding som inneholder opplysninger om utbetaling av sykepenger. Dine rettigheter som
        arbeidsgiver er avledet fra den sykmeldtes rettigheter til sykepenger.
        Periode
        12.06.2026 – 14.07.2026 (33 dager)
        Utbetalt refusjon
        30 004 kr
        Slik har Nav beregnet refusjonen
        Arbeidsgiverperioden og helger er ikke med i summen.
        Periode Type Krevd refusjon Utbetalt av Nav
        27.05.2026 –
        11.06.2026
        Arbeidsgiverperiode
        16 dager
        12.06.2026 Sykepenger
        1 dag
        50 000 kr/mnd
        2 308 kr/dag
        2 308 kr
        13.06.2026 –
        14.06.2026
        Helg
        2 dager
        15.06.2026 –
        19.06.2026
        Sykepenger
        5 dager
        50 000 kr/mnd
        2 308 kr/dag
        11 540 kr
        20.06.2026 –
        21.06.2026
        Helg
        2 dager
        22.06.2026 –
        26.06.2026
        Sykepenger
        5 dager
        50 000 kr/mnd
        2 308 kr/dag
        11 540 kr
        27.06.2026 –
        28.06.2026
        Helg
        2 dager
        29.06.2026 –
        30.06.2026
        Sykepenger
        2 dager
        50 000 kr/mnd
        2 308 kr/dag
        4 616 kr
        01.07.2026 –
        14.07.2026
        Ingen refusjon
        Dere har ikke krevd refusjon
        Sum: 30 004 kr 30 004 kr
        Når kan refusjonen bli redusert?
        Refusjonen følger den ansattes rett til sykepenger. Blir refusjonen redusert, kan ikke Nav oppgi grunnen til
        arbeidsgiveren, fordi Nav har taushetsplikt. Den ansatte får begrunnelsen i vedtaket sitt.
        Vanlige grunner er blant annet:
        Den ansatte er delvis sykmeldt. Refusjonen blir da redusert i samme forhold som sykmeldingsgraden.
        Sykepengene beregnes av inntekt opptil 6 ganger grunnbeløpet (6G).
        Den ansatte har ikke rett til sykepenger for dagen.
        Slik regner Nav ut beløpet per virkedag
        Sykepenger utbetales for virkedager, ikke lørdag og søndag (folketrygdloven § 8-11). Et år regnes som 260
        virkedager (§ 8-10 tredje ledd). Beløpet per virkedag er derfor beløpet per måned × 12 / 260.
        """.trimIndent().trim() shouldBe
            FULL_REFUSJON_PDF_TEKST.also { println(it) }
    }

    @Test
    fun `kompleks refusjon PDF har forventet innhold`() {
        """
        Informasjon om refusjon av sykepenger
        20.07.2026 12:34 Refusjons-ID: 6c078f5c-36ca-4c95-b377-1039f6617451
        Ansatt
        Johannes Hanssen
        Fødselsnummer
        02127437127
        Arbeidsgiver
        Nord-Aurdal kommune
        Organisasjonsnummer
        815493000
        Dette er en informasjonsmelding som inneholder opplysninger om utbetaling av sykepenger. Dine rettigheter som
        arbeidsgiver er avledet fra den sykmeldtes rettigheter til sykepenger.
        Periode
        12.06.2026 – 14.07.2026 (33 dager)
        Utbetalt refusjon
        19 849 kr
        Slik har Nav beregnet refusjonen
        Arbeidsgiverperioden og helger er ikke med i summen.
        Periode Type Krevd refusjon Utbetalt av Nav
        27.05.2026 –
        11.06.2026
        Arbeidsgiverperiode
        16 dager
        12.06.2026 Sykepenger
        1 dag
        50 000 kr/mnd
        2 308 kr/dag
        2 308 kr
        13.06.2026 –
        14.06.2026
        Helg
        2 dager
        15.06.2026 –
        18.06.2026
        Sykepenger
        4 dager
        50 000 kr/mnd
        2 308 kr/dag
        9 232 kr
        19.06.2026 Sykepenger
        1 dag
        30 000 kr/mnd
        1 385 kr/dag
        1 385 kr
        20.06.2026 –
        21.06.2026
        Helg
        2 dager
        22.06.2026 –
        24.06.2026
        Sykepenger
        3 dager
        30 000 kr/mnd
        1 385 kr/dag
        4 155 kr
        25.06.2026 Ikke rett til sykepenger
        1 dag
        30 000 kr/mnd
        1 385 kr/dag
        0 kr
        26.06.2026 Sykepenger
        1 dag
        30 000 kr/mnd
        1 385 kr/dag
        1 385 kr
        27.06.2026 –
        28.06.2026
        Helg
        2 dager
        29.06.2026 –
        30.06.2026
        Sykepenger
        2 dager
        30 000 kr/mnd
        1 385 kr/dag
        1 384 kr
        01.07.2026 –
        14.07.2026
        Ingen refusjon
        Dere har ikke krevd refusjon
        Sum: 22 620 kr 19 849 kr
        Når kan refusjonen bli redusert?
        Refusjonen følger den ansattes rett til sykepenger. Blir refusjonen redusert, kan ikke Nav oppgi grunnen til
        arbeidsgiveren, fordi Nav har taushetsplikt. Den ansatte får begrunnelsen i vedtaket sitt.
        Vanlige grunner er blant annet:
        Den ansatte er delvis sykmeldt. Refusjonen blir da redusert i samme forhold som sykmeldingsgraden.
        Sykepengene beregnes av inntekt opptil 6 ganger grunnbeløpet (6G).
        Den ansatte har ikke rett til sykepenger for dagen.
        Slik regner Nav ut beløpet per virkedag
        Sykepenger utbetales for virkedager, ikke lørdag og søndag (folketrygdloven § 8-11). Et år regnes som 260
        virkedager (§ 8-10 tredje ledd). Beløpet per virkedag er derfor beløpet per måned × 12 / 260.
        """.trimIndent().trim() shouldBe
            KOMPLEKS_REFUSJON_PDF_TEKST.also { println(it) }
    }

    @Test
    fun `ikke rett til sykepenger PDF har forventet innhold`() {
        """
        Informasjon om refusjon av sykepenger
        20.07.2026 12:34 Refusjons-ID: a1b2c3d4-0000-4000-8000-000000000003
        Ansatt
        Johannes Hanssen
        Fødselsnummer
        02127437127
        Arbeidsgiver
        Nord-Aurdal kommune
        Organisasjonsnummer
        815493000
        Dette er en informasjonsmelding som inneholder opplysninger om utbetaling av sykepenger. Dine rettigheter som
        arbeidsgiver er avledet fra den sykmeldtes rettigheter til sykepenger.
        Periode
        12.06.2026 – 14.07.2026 (33 dager)
        Utbetalt refusjon
        0 kr
        Slik har Nav beregnet refusjonen
        Arbeidsgiverperioden og helger er ikke med i summen.
        Periode Type Krevd refusjon Utbetalt av Nav
        27.05.2026 –
        11.06.2026
        Arbeidsgiverperiode
        16 dager
        12.06.2026 Ikke rett til sykepenger
        1 dag
        50 000 kr/mnd
        2 308 kr/dag
        0 kr
        13.06.2026 –
        14.06.2026
        Helg
        2 dager
        15.06.2026 –
        19.06.2026
        Ikke rett til sykepenger
        5 dager
        50 000 kr/mnd
        2 308 kr/dag
        0 kr
        20.06.2026 –
        21.06.2026
        Helg
        2 dager
        22.06.2026 –
        26.06.2026
        Ikke rett til sykepenger
        5 dager
        50 000 kr/mnd
        2 308 kr/dag
        0 kr
        27.06.2026 –
        28.06.2026
        Helg
        2 dager
        29.06.2026 –
        30.06.2026
        Ikke rett til sykepenger
        2 dager
        50 000 kr/mnd
        2 308 kr/dag
        0 kr
        01.07.2026 –
        14.07.2026
        Ingen refusjon
        Dere har ikke krevd refusjon
        Sum: 30 004 kr 0 kr
        Når kan refusjonen bli redusert?
        Refusjonen følger den ansattes rett til sykepenger. Blir refusjonen redusert, kan ikke Nav oppgi grunnen til
        arbeidsgiveren, fordi Nav har taushetsplikt. Den ansatte får begrunnelsen i vedtaket sitt.
        Vanlige grunner er blant annet:
        Den ansatte er delvis sykmeldt. Refusjonen blir da redusert i samme forhold som sykmeldingsgraden.
        Sykepengene beregnes av inntekt opptil 6 ganger grunnbeløpet (6G).
        Den ansatte har ikke rett til sykepenger for dagen.
        Slik regner Nav ut beløpet per virkedag
        Sykepenger utbetales for virkedager, ikke lørdag og søndag (folketrygdloven § 8-11). Et år regnes som 260
        virkedager (§ 8-10 tredje ledd). Beløpet per virkedag er derfor beløpet per måned × 12 / 260.
        """.trimIndent().trim() shouldBe
            IKKE_RETT_REFUSJON_PDF_TEKST.also { println(it) }
    }
}
