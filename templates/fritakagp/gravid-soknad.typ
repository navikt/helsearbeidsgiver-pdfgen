#import "/resources/shared.typ": *
#let data = json("/data/fritakagp/gravid-soknad.json")
#set document(title: "Søknad om fritak fra arbeidsgiverperiode - Gravid", author: "Nav")
= Søknad om fritak fra arbeidsgiverperiode - Gravid
#align(left)[#text(fill: rgb("#4a515e"), size: 9pt)[Mottatt #datetime(get(data, "opprettet"))]]
#if get(data, "referansenummer") != none { #align(right)[#text(fill: rgb("#4a515e"), size: 9pt)[søknadsID: #get(data, "referansenummer")]] }
#info("Søknaden gjelder", data, data)
== Termindato
#date(get(data, "termindato"))
== Arbeidssituasjon og miljø
#text(weight: "medium")[Har dere prøvd å tilrettelegge arbeidsdagen slik at den gravide kan jobbe til tross for helseplager?] \
#yes-no(get(data, "tilrettelegge", default: false))
#if get(data, "tilrettelegge") [
  #text(weight: "medium")[Hvilke tiltak har dere forsøkt eller vurdert for at den ansatte kan jobbe:] \
  #for measure in get(data, "tiltak", default: ()) [
    • #if measure == "TILPASSET_ARBEIDSTID" [Fleksibel eller tilpasset arbeidstid] else if measure == "HJEMMEKONTOR" [Hjemmekontor] else if measure == "TILPASSEDE_ARBEIDSOPPGAVER" [Tilpassede arbeidsoppgaver] else [Annet, gi en kort beskrivelse av hva dere har gjort:] \
  ]
  #if get(data, "tiltakBeskrivelse") != none [#get(data, "tiltakBeskrivelse")]
]
#text(weight: "medium")[Har dere forsøkt omplassering til annen jobb?] \
#dash(get(data, "omplassering"))
== Vedlegg til søknad
#if get(data, "harVedlegg") [Dokumentasjon vedlagt] else [#text(fill: rgb("#4a515e"))[Dokumentasjon ikke vedlagt]]
== Innrapporert av
#dash(get(data, "sendtAvNavn"))
