---
title: Linux Installation
description: How to install Flocon Desktop on Linux (x64 and arm64).
---

# Linux Installation Guide

Flocon Desktop is published on [GitHub Releases](https://github.com/openflocon/Flocon/releases) as `.deb` and `.rpm` packages for both x64 and arm64, and in a signed apt / rpm repository.

| Architecture | Debian / Ubuntu | Fedora / RHEL / openSUSE |
|---|---|---|
| x64 (x86_64) | `Flocon-linux-x64.deb` | `Flocon-linux-x64.rpm` |
| arm64 (aarch64) | `Flocon-linux-arm64.deb` | `Flocon-linux-arm64.rpm` |

A portable jar is also attached (`Flocon-linux-x64.jar`, `Flocon-linux-arm64.jar`); it needs a JDK 21 to run.

## Install from the Flocon repository (recommended)

Updates then arrive with your normal `apt upgrade` / `dnf upgrade`.

### Debian / Ubuntu

```bash
sudo install -d -m 0755 /etc/apt/keyrings
sudo curl -fsSL https://openflocon.github.io/Flocon/flocon.gpg -o /etc/apt/keyrings/flocon.gpg
sudo curl -fsSL https://openflocon.github.io/Flocon/flocon.sources -o /etc/apt/sources.list.d/flocon.sources
sudo apt update
sudo apt install flocon android-tools-adb
```

The `flocon.sources` file (deb822 format) contains:

```
Types: deb
URIs: https://openflocon.github.io/Flocon/apt
Suites: stable
Components: main
Signed-By: /etc/apt/keyrings/flocon.gpg
```

One-line alternative (`/etc/apt/sources.list.d/flocon.list`):

```
deb [signed-by=/etc/apt/keyrings/flocon.gpg] https://openflocon.github.io/Flocon/apt stable main
```

### Fedora / RHEL / openSUSE

```bash
sudo curl -fsSL https://openflocon.github.io/Flocon/flocon.repo -o /etc/yum.repos.d/flocon.repo
sudo dnf install flocon android-tools
```

With dnf5 you can use instead: `sudo dnf config-manager addrepo --from-repofile=https://openflocon.github.io/Flocon/flocon.repo`.

The repo file sets `gpgcheck=1` and `repo_gpgcheck=1` with the key at `https://openflocon.github.io/Flocon/flocon.asc`. Check the key fingerprint (`gpg --show-keys flocon.asc`) against the one in the release notes.

## Install a downloaded package

### Debian / Ubuntu

```bash
sudo apt install ./Flocon-linux-x64.deb      # or Flocon-linux-arm64.deb
```

### Fedora / RHEL / openSUSE

```bash
sudo dnf install ./Flocon-linux-x64.rpm      # or Flocon-linux-arm64.rpm
```

## adb

Flocon uses `adb` for Android screenshots, screen recordings and deeplinks. It is not a dependency of the package, install it separately (the repository commands above already include it):

```bash
sudo apt install android-tools-adb   # Debian / Ubuntu
sudo dnf install android-tools       # Fedora
```

Flocon starts from the app menu (Development) or with `/opt/flocon/bin/Flocon`.

## Maintainer runbook: Linux repository

`.github/workflows/linux-repo.yml` (using `scripts/build-linux-repo.sh`) runs when a release is published. It downloads `Flocon-linux-{x64,arm64}.{deb,rpm}`, signs the rpm packages (`rpmsign`), builds the apt metadata (reprepro, signed `Release` / `InRelease`) and the rpm metadata (createrepo_c, `repomd.xml.asc`), and pushes the result to the `gh-pages` branch, served by GitHub Pages. It can be re-run from Actions ("Run workflow" with a release tag).

### One-time setup

1. Generate a dedicated key on a trusted machine (never reuse a personal key):

    ```bash
    cat > flocon-key.batch <<'EOF'
    %echo Generating Flocon repo key
    Key-Type: EDDSA
    Key-Curve: ed25519
    Key-Usage: sign
    Name-Real: Flocon Repository
    Name-Email: champigny.florent@gmail.com
    Expire-Date: 2y
    %commit
    EOF
    gpg --batch --pinentry-mode ask --generate-key flocon-key.batch
    gpg --list-secret-keys --keyid-format long      # note the key id / fingerprint
    ```

2. Back it up offline (password manager and an encrypted USB drive), with a revocation certificate:

    ```bash
    gpg --armor --export-secret-keys <KEYID> > flocon-repo-private.asc
    gpg --output flocon-repo-revoke.asc --gen-revoke <KEYID>
    ```

3. Add the repository secrets `LINUX_REPO_GPG_PRIVATE_KEY` (content of `flocon-repo-private.asc`) and `LINUX_REPO_GPG_PASSPHRASE`, then delete the exported private key from disk.
4. Enable GitHub Pages: Settings, Pages, source = branch `gh-pages`, folder `/ (root)`. The branch is created by the first workflow run. If the site URL differs from `https://openflocon.github.io/Flocon`, set `BASE_URL` in the script.
5. Run the workflow once with an existing release tag and check `https://openflocon.github.io/Flocon/apt/dists/stable/InRelease`.
6. Publish the key fingerprint in the release notes.

### Rotation (or compromise)

1. Before expiry, or immediately on compromise (revoke the old key with the revocation certificate first), generate a new key as above.
2. Replace both secrets.
3. Re-run the workflow for the latest release: it re-signs the metadata and republishes `flocon.gpg` / `flocon.asc`. Old rpm packages already in `gh-pages` (`rpm/*/`) stay signed with the old key: delete or re-sign them, otherwise `gpgcheck` fails for them.
4. Users re-download `flocon.gpg` (apt); for dnf, remove the old imported key (`sudo rpm -e gpg-pubkey-<old>`) so the new one is imported. Announce the rotation in the release notes.
5. To only extend the expiry of the same key: `gpg --edit-key <KEYID>`, `expire`, `save`, then update the secret and re-run the workflow.
