# helsearbeidsgiver-pdfgen

Generering av PDF-dokumenter for helsearbeidsgiver-tjenester (f.eks.sykmelding for arbeidsgiver).

## Getting started
Kjør følgende (krever docker på maskinen)  

```
chmod +x run_development.sh
./run_development.sh
```  
Serveren blir startet.  
Åpne opp den relevante PDF url i nettleseren.  
F.eks: http://0.0.0.0:8080/api/v1/genpdf/sykmelding/sykmelding

Alle endringer til templates osv. blir nå reflektert i nettleseren ved refresh uten å restarte serveren.

## Utvikling av PDF html
PDF-dokumentene genereres fra HTML-maler som ligger i `templates`-mappen.  
Konverteringen gjøres av java biblioteket openPDFtoHTML, denne har noen begrensninger.  
For tips om hvordan å implementere koden riktig referer til dette [dokumentet om PDF Accesibility i openHTMLtoPDF](https://github.com/danfickle/openhtmltopdf/wiki/PDF-Accessibility-(PDF-UA,-WCAG,-Section-508)-Support)

## Tester
Tester er implementert med Kotlin og TestContainers.

Dette betyr at Dockerfilen blir kjørt og systemet er testet end-to-end.

Testene henter .json filer fra `src/test/resources/` og lagrer PDF filene i `build/test-pdf/` med samme navn. 

## pdfgen

Dette repoet implementerer pdfgen, se på [pdfgen repoet](https://github.com/navikt/pdfgen) for mer informasjon.

Dockerfilen som bygges bruker bare disse 3 mappene fra dette repoet:

```Docker
FROM ghcr.io/navikt/pdfgen:xxx

COPY templates /app/templates
COPY fonts /app/fonts
COPY resources /app/resources
```

Et annet eksempel på et pdfgen-basert prosjekt er [sykepengesoknad-pdfgen](https://github.com/navikt/flex-sykepengesoknad-pdfgen/tree/main)

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #helse-arbeidsgiver.
