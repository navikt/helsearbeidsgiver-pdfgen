#!/bin/bash

LOCAL_PORT="8080"
CURRENT_PATH="$(cd "$(dirname "$1")"; pwd)/$(basename "$1")"
VERSION=$(head -n 1 "$(dirname "$0")/Dockerfile" | cut -d':' -f2) # Henter versjon av pdfgen automatisk fra Dockerfile
echo -e "\033[1;33m[INFO] Hentet pdfgen versjon $VERSION automatisk fra Dockerfile\033[0m"

docker pull ghcr.io/navikt/pdfgen:$VERSION

(sleep 3; \
        printf "\r\n"; \
        printf "\r%s\n" "PDFGEN server starter i DEV modus"; \
        printf "\r%s\n" "Endringer i templates, fonts, data eller resources vil automatisk oppdateres i serveren"; \
        printf "\r%s\n" "http://0.0.0.0:$LOCAL_PORT/api/v1/genpdf/sykmelding/sykmelding"; \
        printf "\r%s\n" "http://0.0.0.0:$LOCAL_PORT/api/v1/genpdf/soknad/soknad") &

docker run \
        -v $CURRENT_PATH/templates:/app/templates \
        -v $CURRENT_PATH/fonts:/app/fonts \
        -v $CURRENT_PATH/data:/app/data \
        -v $CURRENT_PATH/resources:/app/resources \
        -p $LOCAL_PORT:8080 \
        -e DISABLE_PDF_GET=false \
        -e DEV_MODE=true \
        -e JDK_JAVA_OPTIONS \
        -it \
        --rm \
        ghcr.io/navikt/pdfgen:$VERSION

