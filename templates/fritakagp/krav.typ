#import "/templates/fritakagp/shared.typ": *

#let render-krav(data, title) = [
= #title
#align(left)[#text(fill: rgb("#4a515e"), size: 9pt)[Mottatt #datetime(get(data, "opprettet"))]]
#if get(data, "referansenummer") != none {
  align(right)[#text(fill: rgb("#4a515e"), size: 9pt)[ID: #get(data, "referansenummer")]]
}
#if get(data, "status") == "SLETTET" {
  block(fill: rgb("#ca5000"), inset: 8pt, radius: 5pt)[#text(fill: white, weight: "medium")[Kravet er annullert]]
}
#if get(data, "status") == "ENDRET" {
  block(fill: rgb("#ca5000"), inset: 8pt, radius: 5pt)[#text(fill: white, weight: "medium")[Kravet er utdatert og erstattet av et nyere krav]]
}
#info("Kravet gjelder", data, data)
== Fraværsperioder
#for item in get(data, "perioder", default: ()) [
  #block(fill: rgb("#eef4f9"), inset: 8pt, radius: 8pt, below: 8pt)[
    #grid(columns: (1fr, auto))[#text(weight: "semibold")[#period(get(item, "fom"), get(item, "tom"))]][#align(right)[Antall dager fravær i perioden: #get(item, "antallDagerMedRefusjon")]]
    #v(4pt)
    #text(size: 9pt, fill: rgb("#4a515e"))[
      Sykmeldingsgrad: #get(item, "graderingProsent") % \ 
      Beregnet månedsinntekt: #get(item, "månedsinntekt") kr \ 
      Dagsats: #get(item, "dagsats") kr \ 
      Beløp periode: #get(item, "belop") kr
    ]
  ]
]
== Innrapporert av
#dash(get(data, "sendtAvNavn"))
]
