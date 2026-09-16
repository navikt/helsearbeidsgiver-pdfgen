#import "/templates/soknad/shared.typ": *

#let data = json("/data/vedtak/vedtak.json")
#set document(title: "Vedtak om sykepenger", author: "Nav", keywords: ("sykepenger", "vedtak", "refusjon"))

#align(center)[#text(fill: red, weight: "bold", size: 14pt)[Intern testversjon – Dokumentet er ikke gyldig]]

= Vedtak om refusjon av sykepenger
#grid(columns: (1fr, 1fr))[
  #text(fill: rgb("#4a515e"), size: 9pt)[Vedtak fattet #datetime(get(data, "vedtakFattetTidspunkt"))]
][
  #align(right)[#text(fill: rgb("#4a515e"), size: 9pt)[Vedtaksperiode-ID: #dash(get(data, "vedtaksperiodeId"))]]
]

#info(
  "Ansatt",
  (fnr: get(data, "fødselsnummer")),
  (orgnr: get(data, "organisasjonsnummer")),
)

#block(fill: rgb("#eef4f9"), inset: 8pt, radius: 8pt)[
  #grid(columns: (1fr, 1fr))[
    #text(weight: "semibold")[Perioden vedtaket gjelder]
  ][
    #align(right)[#period(get(data, "fom"), get(data, "tom"))]
  ]
]

== Vedtak
=== Utfall
#dash(get(data, "vedtaksUtfallTilArbeidsgiver"))

=== Sykepengegrunnlag
#dash(get(data, "sykepengegrunnlag")) kr

=== Har arbeidsgiver ønsket refusjon
#if get(data, "harArbeidsgiverØnsketRefusjon") [Ja] else [Nei]

== Utbetalingsdager
#let payment-days = get(data, "utbetalingsdager", default: ())
#if payment-days.len() == 0 [
  #text(fill: rgb("#4a515e"))[Ingen utbetalingsdager oppgitt.]
] else [
  #for day in payment-days [
    #block(fill: rgb("#eef4f9"), inset: 8pt, radius: 5pt, below: 4pt)[
      #grid(columns: (1fr, 1fr, 1fr))[
        #date(get(day, "dato"))
      ][
        #dash(get(day, "type"))
      ][
        #align(right)[#dash(get(day, "beløpTilArbeidsgiver")) kr]
      ]
    ]
  ]
]

== Detaljer fra saken
=== Skjæringstidspunkt
#date(get(data, "skjæringstidspunkt"))

=== Yrkesaktivitetstype
#dash(get(data, "yrkesaktivitetstype"))

=== Automatisk fattet
#if get(data, "automatiskFattet") [Ja] else [Nei]

=== Saksbehandler
#dash(get(data, "saksbehandlerNavn")) \
#text(fill: rgb("#4a515e"))[#dash(get(data, "saksbehandlerIdent"))]

=== Beslutter
#dash(get(data, "beslutterNavn")) \
#text(fill: rgb("#4a515e"))[#dash(get(data, "beslutterIdent"))]
