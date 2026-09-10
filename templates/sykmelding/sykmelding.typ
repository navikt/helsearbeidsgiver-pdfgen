#import "../shared.typ": *
#let data = json("/data/sykmelding/sykmelding.json")
#set document(title: "Sykmelding", author: "Nav", keywords: ("sykmelding",))
= Sykmelding
#grid(columns: (1fr, 1fr))[
  #text(fill: rgb("#4a515e"), size: 9pt)[Mottatt av Nav #datetime(get(data, "mottattAvNav"))]
][
  #align(right)[#text(fill: rgb("#4a515e"), size: 9pt)[Sykmeldings-ID: #get(data, "sykmeldingId")]]
]
#info("Sykmeldingen gjelder", get(data, "sykmeldt", default: (:)), get(data, "arbeidsgiver", default: (:)))
== Perioder i sykmeldingen
#text(fill: rgb("#4a515e"), size: 9pt)[Syketilfelle fra: #date(get(data, "sykefravaerFom"))]
#for item in get(data, "sykmeldingPerioder", default: ()) {
  let activity = get(item, "aktivitet", default: (:))
  let type = if get(activity, "aktivitetIkkeMulig") != none {
    "100% sykmeldt"
  } else if get(activity, "gradertSykmelding") != none {
    str(get(get(activity, "gradertSykmelding"), "sykmeldingsgrad")) + "% sykmeldt"
  } else if get(activity, "avventendeSykmelding") != none {
    "Avventende sykmelding"
  } else if get(activity, "antallBehandlingsdagerUke") != none {
    "Behandlingsdager"
  } else {
    "Reisetilskudd"
  }
  #block(fill: rgb("#eef4f9"), inset: 8pt, radius: 8pt, below: 8pt)[
    #grid(columns: (1fr, 1fr, 1fr))[
      #text(weight: "semibold")[#type]
    ][#period(get(item, "fom"), get(item, "tom"))][
      #if get(activity, "gradertSykmelding") != none [
        #if get(get(activity, "gradertSykmelding"), "harReisetilskudd") [Med reisetilskudd] else [Uten reisetilskudd]
      ] else if get(get(activity, "aktivitetIkkeMulig", default: (:)), "manglendeTilretteleggingPaaArbeidsplassen") [
        Manglende tilrettelegging på arbeidsplassen
      ] else if get(activity, "antallBehandlingsdagerUke") != none [
        #get(activity, "antallBehandlingsdagerUke") behandlingsdager
      ]
    ]
  ]
}
== Egenmeldingsdager
#let self-reported = get(data, "egenmeldingsdager", default: ())
#if self-reported.len() == 0 [
  #text(fill: rgb("#4a515e"))[Ingen egenmeldingsdager oppgitt.]
] else [
  #text(fill: rgb("#4a515e"))[Oppgitt av ansatt selv ved bekreftelse av sykmelding.]
  #for item in self-reported [#period(get(item, "fom"), get(item, "tom")) \ ]
]
== Oppfølging
#let followup = get(data, "oppfoelging", default: (:))
#if get(followup, "prognose") != none [
  #text(weight: "medium")[Prognose og hensyn etter sykefravær]
  #let prognosis = get(followup, "prognose")
  #if get(prognosis, "erArbeidsfoerEtterEndtPeriode") [• Arbeidsfør etter endt periode \ ]
  #if get(prognosis, "beskrivHensynArbeidsplassen") != none [#get(prognosis, "beskrivHensynArbeidsplassen") \ ]
]
#if get(followup, "tiltakArbeidsplassen") != none [#text(weight: "medium")[Tiltak som kan bedre ansattes arbeidsevne] \ #get(followup, "tiltakArbeidsplassen") \ ]
#if get(followup, "meldingTilArbeidsgiver") != none [#text(weight: "medium")[Melding fra behandler til arbeidsgiver] \ #get(followup, "meldingTilArbeidsgiver") \ ]
#for item in get(data, "sykmeldingPerioder", default: ()) {
  let activity = get(item, "aktivitet", default: (:))
  if get(get(activity, "aktivitetIkkeMulig", default: (:)), "beskrivelse") != none [
    #text(weight: "medium")[Forhold på arbeidsplassen vanskeliggjør arbeidsrelatert aktivitet] \
    #get(get(activity, "aktivitetIkkeMulig"), "beskrivelse") \
  ]
  if get(activity, "avventendeSykmelding") != none [
    #text(weight: "medium")[Innspill til arbeidsgiver om tilrettelegging] \
    #get(activity, "avventendeSykmelding") \
  ]
}
#if get(followup, "prognose") == none and get(followup, "tiltakArbeidsplassen") == none and get(followup, "meldingTilArbeidsgiver") == none [
  Ingen oppfølging oppgitt.
]
#v(2em)
#line(length: 100%, stroke: rgb("#e0e0e0"))
#v(1em)
#block(width: 50%, fill: rgb("#e6f0f7"), inset: 10pt, radius: 5pt)[
  #text(size: 8pt, weight: "semibold")[Sykmelding skrevet av] \
  #text(size: 16pt, weight: "medium")[#dash(get(get(data, "behandler", default: (:)), "navn"))] \
  #text(size: 8pt, weight: "semibold")[Telefon] \
  #dash(get(get(data, "behandler", default: (:)), "tlf")) \
  #text(size: 8pt, weight: "semibold")[Kontakt med pasient] \
  #datetime(get(data, "kontaktMedPasient"))
]
