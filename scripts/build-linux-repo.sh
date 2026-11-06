#!/usr/bin/env bash
# Builds/updates the signed apt + rpm repository tree.
# Usage: build-linux-repo.sh <assets-dir> <site-dir> <gpg-key-id>
# <assets-dir> contains Flocon-linux-{x64,arm64}.{deb,rpm}
# <site-dir>   is the checked-out gh-pages tree (updated in place)
# Requires: reprepro, createrepo_c, rpm (rpmsign), gnupg, dpkg-dev.
# The secret key must be usable non-interactively (agent preset or no passphrase).
set -euo pipefail

ASSETS=${1:?assets dir}
SITE=${2:?site dir}
KEY=${3:?gpg key id}
BASE_URL=${BASE_URL:-https://openflocon.github.io/Flocon}

mkdir -p "$SITE/apt/conf" "$SITE/rpm/x86_64" "$SITE/rpm/aarch64"

# --- public keys, stable URLs -------------------------------------------------
gpg --batch --yes --export "$KEY" > "$SITE/flocon.gpg"            # dearmored, for apt signed-by
gpg --batch --yes --armor --export "$KEY" > "$SITE/flocon.asc"    # armored, for rpm / deb822

# --- apt ----------------------------------------------------------------------
cat > "$SITE/apt/conf/distributions" <<DIST
Origin: Flocon
Label: Flocon
Codename: stable
Suite: stable
Architectures: amd64 arm64
Components: main
Description: Flocon Desktop
SignWith: $KEY
DIST

for deb in "$ASSETS"/Flocon-linux-*.deb; do
  [ -e "$deb" ] || continue
  name=$(dpkg-deb -f "$deb" Package)
  ver=$(dpkg-deb -f "$deb" Version)
  arch=$(dpkg-deb -f "$deb" Architecture)
  # make re-runs for the same release idempotent
  reprepro -b "$SITE/apt" removefilter stable \
    "Package (== $name), \$Version (== $ver), \$Architecture (== $arch)" || true
  reprepro -b "$SITE/apt" includedeb stable "$deb"
done
reprepro -b "$SITE/apt" export stable

# --- rpm ----------------------------------------------------------------------
cat > "$HOME/.rpmmacros" <<MACROS
%_gpg_name $KEY
%__gpg /usr/bin/gpg
%_gpg_digest_algo sha256
MACROS

for rpmf in "$ASSETS"/Flocon-linux-*.rpm; do
  [ -e "$rpmf" ] || continue
  arch=$(rpm -qp --queryformat '%{ARCH}' "$rpmf")
  nvr=$(rpm -qp --queryformat '%{NAME}-%{VERSION}-%{RELEASE}' "$rpmf")
  rpmsign --addsign "$rpmf"
  cp "$rpmf" "$SITE/rpm/$arch/$nvr.$arch.rpm"
done

for arch in x86_64 aarch64; do
  createrepo_c --update "$SITE/rpm/$arch"
  gpg --batch --yes --armor --detach-sign \
    --output "$SITE/rpm/$arch/repodata/repomd.xml.asc" "$SITE/rpm/$arch/repodata/repomd.xml"
done

cat > "$SITE/flocon.repo" <<REPO
[flocon]
name=Flocon Desktop
baseurl=$BASE_URL/rpm/\$basearch
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=$BASE_URL/flocon.asc
REPO

cat > "$SITE/flocon.sources" <<SRC
Types: deb
URIs: $BASE_URL/apt
Suites: stable
Components: main
Signed-By: /etc/apt/keyrings/flocon.gpg
SRC

touch "$SITE/.nojekyll"
