package sykmelding

import SYKMELDING_PDF_ROUTE
import hentPdf
import lagPdfOgHentTekst
import org.apache.pdfbox.Loader
import org.apache.pdfbox.cos.COSDictionary
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkedContentReference
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureNode
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot
import org.apache.pdfbox.text.PDFMarkedContentExtractor
import org.apache.pdfbox.text.TextPosition
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestMethodOrder
import org.verapdf.gf.foundry.VeraGreenfieldFoundryProvider
import org.verapdf.pdfa.Foundries
import org.verapdf.pdfa.flavours.PDFAFlavour
import org.verapdf.pdfa.results.TestAssertion
import skalIkkeInneholde
import skalInneholde
import java.io.File
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

    @Test
    fun `test pdf should be compliant with PDF-UA standards`() {
        // / 1. Initialize Greenfield (Native veraPDF parser)
        VeraGreenfieldFoundryProvider.initialise()

        val jsonData = File("src/test/resources/fullstendig_sykmelding.json").readText()
        val pdfBytes = hentPdf(SYKMELDING_PDF_ROUTE, jsonData)

        // 2. Load the PDF/UA-1 Flavour
        val flavour = PDFAFlavour.PDFUA_1

        // 3. Validate
        pdfBytes.inputStream().use { inputStream ->
            Foundries.defaultInstance().createParser(inputStream, flavour).use { parser ->
                val validator = Foundries.defaultInstance().createValidator(flavour, false)
                val result = validator.validate(parser)

                if (!result.isCompliant) {
                    println("\n--- PDF STRUCTURE ANALYSIS ---")

                    result.testAssertions
                        .filter { it.status == TestAssertion.Status.FAILED }
                        .forEach { assertion ->
                            // 1. Human-friendly message
                            val simpleMsg = assertion.message.split(".")[0] // Just the first sentence
                            println("\nISSUE: $simpleMsg")
                            println("RULE ID: ${assertion.ruleId.testNumber}")

                            // 2. The "Breadcrumb" Path
                            // We clean the long string: "root/document[0]/StructTreeRoot[0]..."
                            val cleanPath =
                                assertion.location.context
                                    .split("/")
                                    .filter { it.contains("obj ") }
                                    .joinToString(" > ") { it.substringAfterLast("obj ").substringBefore(")").substringAfter(" ") }

                            println("PATH: $cleanPath")
                        }
                }

                // load pdfbytes into pdfbox
                val pdDoc = Loader.loadPDF(pdfBytes)
                val structureTreeRoot = pdDoc.documentCatalog.structureTreeRoot
                if (structureTreeRoot != null) {
                    printTagTree(structureTreeRoot)
                }

//                if (!result.isCompliant) {
//                    result.testAssertions
//                        .filter { it.status != org.verapdf.pdfa.results.TestAssertion.Status.PASSED }
//                        .forEach { println("Failure: ${it.message} (Rule: ${it.ruleId})") }
//                }

                assertTrue(result.isCompliant, "PDF is not compliant with PDF/UA-1")
            }
        }
    }

    fun printTagTree(
        node: PDStructureNode,
        depth: Int = 0,
    ) {
        val indent = "  ".repeat(depth)

        when (node) {
            is PDStructureTreeRoot -> {
                println("$indent[StructureTreeRoot]")
                node.kids?.forEach { kid ->
                    when (kid) {
                        is PDStructureNode -> {
                            printTagTree(kid, depth + 1)
                        }

                        is COSDictionary -> {
                            try {
                                printTagTree(PDStructureNode.create(kid), depth + 1)
                            } catch (_: Exception) {
                                // skip non-structure entries
                            }
                        }
                    }
                }
            }

            is PDStructureElement -> {
                val tag = node.structureType ?: "Unknown"
                val text = extractTextFromElement(node).trim()

                if (text.isNotEmpty()) {
                    println("$indent<$tag> $text")
                } else {
                    println("$indent<$tag>")
                }

                node.kids?.forEach { kid ->
                    when (kid) {
                        is PDStructureNode -> {
                            printTagTree(kid, depth + 1)
                        }

                        is COSDictionary -> {
                            try {
                                printTagTree(PDStructureNode.create(kid), depth + 1)
                            } catch (_: Exception) {
                                // leaf node or non-structure content, skip
                            }
                        }
                    }
                }
            }
        }
    }

    fun extractTextFromElement(element: PDStructureElement): String {
        val sb = StringBuilder()
        element.kids?.forEach { kid ->
            when (kid) {
                is org.apache.pdfbox.cos.COSString -> {
                    sb.append(kid.string)
                }

                is org.apache.pdfbox.cos.COSArray -> {
                    kid.forEach { item ->
                        if (item is org.apache.pdfbox.cos.COSString) sb.append(item.string)
                    }
                }

                is org.apache.pdfbox.cos.COSBase -> {
                    try {
                        val child = PDStructureNode.create(kid as? COSDictionary)
                        if (child is PDStructureElement) {
                            sb.append(extractTextFromElement(child))
                        }
                    } catch (_: Exception) {
                        // skip
                    }
                }
            }
        }
        return sb.toString()
    }

    @TestFactory
    fun `sykmelding PDF har involverte parter`(): List<DynamicTest> =
        PDF_TEKST.skalInneholde(
            "Sykmeldingen gjelder",
            "sykmeldt_navn \nsom_er_veldig_langt \nfor_å_teste_formattering",
            "Fødselsnummer",
            "sykmeldt_fnr",
            "Arbeidsgiver",
            "arbeidsgiver_navn \nsom_også_er_veldig_langt_AS",
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
            "Syketilfelle fra: 22.05.2025",
            "100% sykmeldt 24.05.2025 – 24.05.2025 (1 dag)",
            "100% sykmeldt 25.05.2025 – 26.05.2025 (2 dager) Manglende tilrettelegging på arbeidsplassen",
            "50% sykmeldt 28.05.2025 – 30.05.2025 (3 dager) Med reisetilskudd",
            "50% sykmeldt 01.06.2025 – 04.06.2025 (4 dager) Uten reisetilskudd",
            "Avventende sykmelding 06.06.2025 – 10.06.2025 (5 dager)",
            "Behandlingsdager 12.06.2025 – 17.06.2025 (6 dager) 1 behandlingsdager",
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
