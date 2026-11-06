#!/usr/bin/env bash
# Fill the version, date and sha256 placeholders of the Flatpak files.
# Usage: render-manifest.sh <version> <assets-dir> <output-dir>
#   <assets-dir> holds Flocon-linux-x64.deb and Flocon-linux-arm64.deb (release assets).
#   The platform-tools zip is downloaded to compute its sha256 (or set PLATFORM_TOOLS_SHA256).
set -euo pipefail

VERSION="${1:?version}"
ASSETS="${2:?assets dir}"
OUT="${3:?output dir}"
HERE="$(cd "$(dirname "$0")" && pwd)"
PT_URL="https://dl.google.com/android/repository/platform-tools_r35.0.2-linux.zip"

SHA_X64="$(sha256sum "$ASSETS/Flocon-linux-x64.deb" | cut -d' ' -f1)"
SHA_ARM64="$(sha256sum "$ASSETS/Flocon-linux-arm64.deb" | cut -d' ' -f1)"
if [ -z "${PLATFORM_TOOLS_SHA256:-}" ]; then
  PLATFORM_TOOLS_SHA256="$(curl -fsSL "$PT_URL" | sha256sum | cut -d' ' -f1)"
fi
DATE="$(date -u +%F)"

mkdir -p "$OUT"
cp "$HERE/io.github.openflocon.flocon.desktop" "$HERE/io.github.openflocon.flocon.png" "$OUT/"
for f in io.github.openflocon.flocon.yml io.github.openflocon.flocon.metainfo.xml; do
  sed -e "s/__VERSION__/$VERSION/g" \
      -e "s/__DATE__/$DATE/g" \
      -e "s/__SHA256_X64__/$SHA_X64/g" \
      -e "s/__SHA256_ARM64__/$SHA_ARM64/g" \
      -e "s/__SHA256_PLATFORM_TOOLS__/$PLATFORM_TOOLS_SHA256/g" \
      "$HERE/$f" > "$OUT/$f"
done
