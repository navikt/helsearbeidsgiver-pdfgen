# AGENTS.md

Repoet lager PDF-er for helsearbeidsgiver-tjenester med [navikt/pdfgen](https://github.com/navikt/pdfgen). En PDF er en Handlebars-mal (`.hbs`) som openHTMLtoPDF gjør om fra HTML til PDF. Andre tjenester sender ferdig JSON hit og får PDF tilbake. Tjenestene som har tilgang, står under `accessPolicy.inbound` i `.nais/nais.yaml` (bl.a. `sykepenger-im-lps-api`, `hag-dokument-proxy` og `refusjon`).

## Struktur

```
templates/<app>/<mal>.hbs          Malen for én PDF
templates/partials/*.hbs           Delte partials for alle PDF-er
templates/partials/<app>/*.hbs     Partials for én PDF (f.eks. partials/refusjon/)
templates/style.hbs                Felles CSS, lastes med {{#block 'style'}}{{/block}}
data/<app>/<mal>.json              Eksempeldata for lokal utvikling
src/test/resources/*.json          Testdata for regresjonstestene
src/test/kotlin/<Navn>Test.kt      Regresjonstest per PDF
resources/                         Bilder og SVG-er
fonts/                             Source Sans Pro
```

Navnet på mappe og fil gir endepunktet. `templates/refusjon/refusjon.hbs` blir `/api/v1/genpdf/refusjon/refusjon`. Lokalt henter GET-kall data fra `data/refusjon/refusjon.json`.

## Lokal utvikling

Start serveren selv med `./run_development.sh`. Skriptet stopper ikke av seg selv, så kjør det som bakgrunnsprosess. Sjekk først om port 8080 svarer (`curl -s http://127.0.0.1:8080/`), da kjører den allerede.

Skriptet krever Docker. Det starter pdfgen i dev-modus på port 8080 og skriver ut lenker til alle PDF-endepunktene, for eksempel:

```
http://127.0.0.1:8080/api/v1/genpdf/refusjon/refusjon
```

Gi brukeren direkte lenke til PDF-en dere jobber med.

Serveren leser `templates`, `fonts`, `data` og `resources` direkte fra disk. Etter en endring trenger du ikke starte serveren på nytt. Si til brukeren at de må laste siden på nytt i nettleseren for å se endringen.

Feiler oppstarten, les feilmeldingen. Vanlige årsaker er at Docker eller Colima ikke kjører, advarselen om Apple Silicon uten Rosetta, eller «the input device is not a TTY» (skriptet bruker `docker run -it`). Be da brukeren fikse det og starte skriptet selv.

## Skriv Handlebars som ligner resten av repoet

Følg strukturen og kodestilen i de andre PDF-ene. Målet er at malene er lette å lese.

- Bruk eksisterende partials før du lager nye:
  - `partials/sykmeldtOgArbeidsgiverInfo` for boksene med ansatt og arbeidsgiver
  - `partials/periodeMedAntallDager` for «fom – tom (n dager)»
  - `partials/valueOrDash` for verdier som kan mangle
- Trekk ut en partial når en blokk gjentas eller gjør hovedmalen vanskelig å lese. Partials for én PDF legges i `templates/partials/<app>/`.
- Send parametere eksplisitt til partials (`{{> partials/valueOrDash v=navn }}`). Da ser du hva partialen trenger.
- Bruk pdfgen-hjelperne som allerede er i bruk: `eq`, `if`, `each`, `iso_to_date`, `iso_to_nor_datetime`, `currency_no`, `duration`, `inc`, `contains_field`.
- Start malen med samme `<head>` som de andre: XHTML-doctype, `<title>`, `meta`, `bookmarks` og `{{#block 'style'}}{{/block}}`. CSS som bare gjelder én PDF, legges i en `<style>` under blokken.
- Bruk `div` med `display: table` (klassene `table`, `row`, `cell`) til layout, ikke `<table>`. openHTMLtoPDF anbefaler dette for tilgjengelighet. Se [openHTMLtoPDF om PDF-tilgjengelighet](https://github.com/danfickle/openhtmltopdf/wiki/PDF-Accessibility-(PDF-UA,-WCAG,-Section-508)-Support).
- openHTMLtoPDF støtter CSS 2.1 og litt mer. Flexbox og grid virker ikke. Sjekk resultatet i PDF-en, ikke i en vanlig nettleser.
- Skriv kommentarer med `{{!-- --}}` bare når noe trenger forklaring.

## Tilgjengelighet

PDF-ene skal være tilgjengelige. Følg god praksis og gjør det på samme måte som i `sykmelding.hbs`. Det er ikke et krav om full PDF/UA-sertifisering, men nye maler skal ikke være dårligere enn de som finnes.

- Sett `lang="no"` på `<html>`, og fyll ut `<title>` og `meta` (`description`, `subject`, `author`, `keywords`).
- Legg til `<bookmarks>` med en bookmark per hovedseksjon. Hver bookmark peker på en `id` i dokumentet.
- Bruk overskrifter i riktig rekkefølge: én `<h1>`, så `<h2>` per seksjon og `<h3>` under. Ikke hopp over nivåer, og ikke bruk overskrifter bare for å få stor tekst. `style.hbs` nullstiller marginene, så `<h3>` kan brukes uten å endre utseendet.
- Bruk `<p>` for tekst, ikke løs tekst rett i `div`. Skjermlesere trenger elementene.
- Bruk `<ul>`/`<li>` for lister.
- Gi alle bilder en `alt` som beskriver innholdet. Er bildet bare pynt og teksten ved siden av sier det samme, bruk `alt=""`.
- Bruk ikke farge alene for å vise betydning. Skriv også teksten, for eksempel «Ikke rett til sykepenger».
- Hold god kontrast. Grå tekst på grå bakgrunn bør være minst 4,5:1.
- Bygg tabellignende layout med `div` og `display: table` i lesbar rekkefølge, slik at teksten gir mening når den leses fra topp til bunn. Regresjonstesten viser denne rekkefølgen.

## Logikk hører hjemme hos avsenderen

Vi har lite kontroll over hva Handlebars kan gjøre i pdfgen. Enkle `if`, `eq` og `each` og formatering med hjelperne går fint. Summering, gruppering, sortering, beregning og regler skal gjøres i tjenesten som sender JSON.

JSON-en skal være ferdig behandlet:

- Beløp og summer er ferdig regnet ut.
- Perioder er ferdig delt opp og sortert.
- Typer er enum-verdier malen kan sjekke med `eq` (f.eks. `"type": "SYKEPENGER"`).
- Felt som skal skjules, er utelatt eller `null`.

Trenger en PDF ny logikk, foreslå et nytt felt i JSON-en i stedet for å bygge logikken i malen.

## Regresjonstester

Testene starter pdfgen i Docker med Testcontainers, sender JSON fra `src/test/resources/` og henter ut teksten fra PDF-en med PDFBox. Teksten sammenlignes med forventet tekst i testen. PDF-ene lagres i `build/test-pdf/<json-navn>.pdf`, så du kan åpne dem.

```bash
./gradlew test                        # alle tester (krever Docker)
./gradlew test --tests RefusjonTest   # én PDF
./gradlew formatKotlin lintKotlin     # ktlint
```

GitHub Actions kjører `./gradlew test` på alle pull requests.

### Når du endrer en mal

Kjør testene for PDF-en etter hver endring i en mal, partial eller `style.hbs`. Endringer i partials og `style.hbs` påvirker flere PDF-er, så kjør da alle testene.

Hvis testen feiler, les diffen linje for linje:

- **Forventet endring.** Du endret teksten med vilje. Oppdater forventet tekst i testen.
- **Uventet endring.** Tekst du ikke endret, er flyttet, borte eller ny. Det kan være en `if` som slår feil, et felt som mangler, «undefined», en tom verdi eller tekst fra en partial som lekker inn. Rett malen, ikke testen.

Oppdater aldri forventet tekst bare for å få testen grønn. Si til brukeren hvilke tekstendringer som er forventet.

PDF-tekst får ofte mellomrom på slutten av linjer som brytes. `RefusjonTest` fjerner dem med `utenMellomromPaaLinjeslutt()`.

### Ny PDF eller nytt tilfelle

1. Lag malen i `templates/<app>/<mal>.hbs`.
2. Legg eksempeldata i `data/<app>/<mal>.json`, så PDF-en virker i dev-serveren.
3. Legg testdata i `src/test/resources/`. Lag minst ett minimalt og ett fullstendig eksempel. Følg navnene som finnes (`minimal_sykmelding.json`, `fullstendig_sykmelding.json`, `refusjon_kompleks.json`).
4. Legg ruten som konstant i `src/test/kotlin/Utils.kt`.
5. Lag `<Navn>Test.kt` etter mønsteret i `SykmeldingTest.kt` og `RefusjonTest.kt`: hent tekst med `lagPdfOgHentTekst` og sammenlign med `shouldBe`.
6. Kjør testen. Les den genererte teksten i loggen og PDF-en i `build/test-pdf/` før du limer teksten inn som forventet.

Bruk lesbare testverdier som viser hvor feltet havner (f.eks. `"sykmeldt_navn"`), eller realistiske data når layout og tall skal sjekkes. Bruk aldri ekte personopplysninger.

## Bygg og deploy

`Dockerfile` bygger på `ghcr.io/navikt/pdfgen` og kopierer bare `templates`, `fonts` og `resources`. `data/` er bare for lokal utvikling. pdfgen-versjonen står i `Dockerfile`, og `run_development.sh` leser den derfra. Nais-oppsettet ligger i `.nais/`.
