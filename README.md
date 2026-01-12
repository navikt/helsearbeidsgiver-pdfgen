# helsearbeidsgiver-pdfgen

Generering av PDF-dokumenter for helsearbeidsgiver-tjenester (f.eks.sykmelding for arbeidsgiver).


## Tester
Tester er implementert med Kotlin og TestContainers.

Dette betyr at Dockerfilen blir kjørt og systemet er testet end-to-end.

Når testene kjøres lagres PDF-ene produsert i `build/test-pdf/`

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
