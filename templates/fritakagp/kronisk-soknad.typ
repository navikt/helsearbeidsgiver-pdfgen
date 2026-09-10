#import "../shared.typ": *
#let data = json("/data/fritakagp/kronisk-soknad.json")
#set document(title: "Søknad om fritak fra arbeidsgiverperiode - kronisk syk", author: "Nav")
= Søknad om fritak fra arbeidsgiverperiode - kronisk syk
#align(left)[#text(fill: rgb("#4a515e"), size: 9pt)[Mottatt #datetime(get(data, "opprettet"))]]
#if get(data, "referansenummer") != none { #align(right)[#text(fill: rgb("#4a515e"), size: 9pt)[søknadsID: #get(data, "referansenummer")]] }
#info("Søknaden gjelder", data, data)
== Vedlegg til søknad
#text(fill: if get(data, "harVedlegg") { rgb("#262626") } else { rgb("#4a515e") })[#if get(data, "harVedlegg") [Dokumentasjon vedlagt] else [Dokumentasjon ikke vedlagt]]
== Historisk fravær
#if get(data, "ikkeHistoriskFravaer") [
  Det finnes ikke historisk fravær på grunn av nyansettelse, lengre permisjon eller annet.
] else [
  #for item in get(data, "fravaerPerAar", default: ()) [#text(weight: "medium")[#get(item, "aar"): ]#get(item, "antallDager") dager \ ]
  #text(weight: "medium")[Antall fraværsperioder siste 2 år: #get(data, "antallPerioder")]
]
== Innrapporert av
#dash(get(data, "sendtAvNavn"))
