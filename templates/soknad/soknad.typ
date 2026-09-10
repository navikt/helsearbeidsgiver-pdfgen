#import "/resources/shared.typ": *
#let root = json("/data/soknad/soknad.json")
#let data = get(root, "sykepengesoeknad", default: (:))
#set document(title: "Søknad om sykepenger", author: "Nav")
= Søknad om sykepenger
#grid(columns: (1fr, 1fr))[
  #text(fill: rgb("#4a515e"), size: 9pt)[Mottatt av Nav #datetime(get(data, "mottatTid"))]
][
  #align(right)[#text(fill: rgb("#4a515e"), size: 9pt)[Søknads-ID: #get(data, "soeknadId") \ Sykmeldings-ID: #get(data, "sykmeldingId")]]
]
#info("Søknaden gjelder", (navn: get(root, "sykmeldtNavn"), fnr: get(data, "fnr")), get(data, "arbeidsgiver", default: (:)))
== Søknadsperioder
#for item in get(data, "soeknadsperioder", default: ()) {
  let kind = get(item, "sykmeldingstype")
  let title = if kind == "AKTIVITET_IKKE_MULIG" { "100% sykmeldt" } else if kind == "GRADERT" { str(get(item, "sykmeldingsgrad")) + "% sykmeldt" } else if kind == "BEHANDLINGSDAGER" { "Behandlingsdager" } else if kind == "AVVENTENDE_SYKMELDING" { "Avventende sykmelding" } else { "Reisetilskudd" }
  #block(fill: rgb("#eef4f9"), inset: 8pt, radius: 8pt, below: 8pt)[
    #grid(columns: (1fr, 1fr))[#text(weight: "semibold")[#title]][#align(right)[#period(get(item, "fom"), get(item, "tom"))]]
    #text(size: 9pt, fill: rgb("#4a515e"))[Sykmeldingsgrad: #dash(get(item, "sykmeldingsgrad"))% \ Arbeidsuke: #dash(get(item, "avtaltTimer")) t \ Faktisk arbeidsgrad: #dash(get(item, "faktiskGrad"))% \ Arbeidet i perioden: #dash(get(item, "faktiskTimer")) t]
  ]
}
#text(weight: "medium")[Arbeid gjenopptatt] \
#date(get(data, "arbeidGjenopptattDato"))
#if get(data, "behandlingsdager") != none [
  == Behandlingsdager
  #text(fill: rgb("#4a515e"))[Dager med behandlingsdager oppgitt i søknaden.]
  #for item in get(data, "behandlingsdager") [• #date(item) \ ]
]
== Fraværsperioder
#let absences = get(data, "fravaer", default: ())
#if absences.len() == 0 [
  #text(fill: rgb("#4a515e"))[Ingen fraværsperioder oppgitt.]
] else [
  #for item in absences {
    let kind = get(item, "type")
    let title = if kind == "FERIE" { "Ferie" } else if kind == "PERMISJON" { "Permisjon" } else { "Opphold i utlandet utenfor EU/EØS" }
    #block(fill: rgb("#eef4f9"), inset: 8pt, radius: 8pt, below: 8pt)[#grid(columns: (1fr, 1fr))[#text(weight: "semibold")[#title]][#align(right)[#period(get(item, "fom"), get(item, "tom"))]]]
  }
]
