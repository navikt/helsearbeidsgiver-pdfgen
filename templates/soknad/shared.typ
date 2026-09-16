#set page(
  paper: "a4",
  margin: (x: 1cm, y: 1cm),
)
#set text(font: "Source Sans Pro", size: 10pt, fill: rgb("#262626"))
#set par(leading: 0.4em)
#set heading(numbering: none)
#show heading.where(level: 1): it => {
  set text(size: 22pt, weight: "medium")
  it
}
#show heading.where(level: 2): it => {
  v(1em)
  line(length: 100%, stroke: rgb("#e0e0e0"))
  v(0.5em)
  set text(size: 14pt, weight: "medium")
  it
}

#let get(data, key, default: none) = if type(data) == dictionary and key in data { data.at(key) } else { default }
#let dash(value) = if value == none or value == "" { "—" } else { str(value) }
#let date(value) = {
  let value = str(value)
  if value.len() < 10 { return dash(value) }
  value.slice(8, 10) + "." + value.slice(5, 7) + "." + value.slice(0, 4)
}
#let datetime(value) = {
  let value = str(value)
  if value.len() < 16 { return dash(value) }
  date(value) + " " + value.slice(11, 16)
}
#let period(from, to) = {
  let start = date(from)
  let end = date(to)
  if start == end { start } else { start + " – " + end }
}
#let yes-no(value) = if value { "Ja" } else { "Nei" }
#let info(label, person, employer) = {
  grid(
    columns: (1fr, 1fr),
    gutter: 8pt,
    inset: 10pt,
    fill: (rgb("#00213d"), rgb("#e6f0f7")),
    [
      #text(fill: white, size: 8pt, weight: "semibold")[#label] \
      #text(fill: white, size: 16pt, weight: "medium")[#dash(get(person, "navn"))] \
      #text(fill: white, size: 8pt, weight: "semibold")[Fødselsnummer] \
      #text(fill: white)[#dash(get(person, "fnr", default: get(person, "identitetsnummer")))]
    ],
    [
      #text(size: 8pt, weight: "semibold")[Arbeidsgiver] \
      #text(size: 16pt, weight: "medium")[#dash(get(employer, "navn", default: get(employer, "virksomhetsnavn")))] \
      #text(size: 8pt, weight: "semibold")[Organisasjonsnummer] \
      #dash(get(employer, "orgnr", default: get(employer, "virksomhetsnummer")))
    ],
  )
  v(1em)
}
