#!/bin/bash

set -e

LOCAL_PORT="8080"
CURRENT_PATH="$(cd "$(dirname "$0")"; pwd)"
VERSION=$(head -n 1 "$CURRENT_PATH/Dockerfile" | cut -d':' -f2)
CONTAINER_NAME="pdfgen-dev-$$"

if [[ "$(uname -m)" == "arm64" ]]; then
    export JDK_JAVA_OPTIONS="${JDK_JAVA_OPTIONS:+$JDK_JAVA_OPTIONS }-XX:UseAVX=0"
    echo -e "\033[1;33m[INFO] Apple Silicon oppdaget – setter JDK_JAVA_OPTIONS=\"$JDK_JAVA_OPTIONS\" for QEMU-kompatibilitet\033[0m"
fi

echo -e "\033[1;33m[INFO] Hentet pdfgen versjon $VERSION automatisk fra Dockerfile\033[0m"
docker pull ghcr.io/navikt/pdfgen:$VERSION


( # Vent for serveren å starte før melding printes
    for i in {1..120}; do
        if docker logs "$CONTAINER_NAME" 2>&1 | grep -q "Responding at http://0.0.0.0:"; then
            printf "\r\n"
            printf "\r%s\n" "$(echo -e "\033[1;32mPDFGEN server online (DEV mode)\033[0m")"
            printf "\r%s\n" "Endringer i templates, fonts, data eller resources vil automatisk oppdateres i serveren"
            printf "\r%s\n" "PDF endepunkt:"
            curl -s "http://0.0.0.0:$LOCAL_PORT/" | tail -n +2 | grep -v '/partials' | while IFS= read -r line; do printf "\r%s\n" "http://127.0.0.1:$LOCAL_PORT$line"; done
            printf "\r\n"
            exit 0
        fi
        sleep 1
    done
) &

docker run \
    --name "$CONTAINER_NAME" \
    -v "$CURRENT_PATH/templates:/app/templates" \
    -v "$CURRENT_PATH/fonts:/app/fonts" \
    -v "$CURRENT_PATH/data:/app/data" \
    -v "$CURRENT_PATH/resources:/app/resources" \
    -p "$LOCAL_PORT:8080" \
    -e DISABLE_PDF_GET=false \
    -e DEV_MODE=true \
    -e JDK_JAVA_OPTIONS \
    -it \
    --rm \
    ghcr.io/navikt/pdfgen:$VERSION

