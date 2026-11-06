---
title: Linux Installation
description: How to install Flocon Desktop on Linux (x64 and arm64).
---

# Linux Installation Guide

Flocon Desktop is published on [GitHub Releases](https://github.com/openflocon/Flocon/releases) as `.deb` and `.rpm` packages for both x64 and arm64.

| Architecture | Debian / Ubuntu | Fedora / RHEL / openSUSE |
|---|---|---|
| x64 (x86_64) | `Flocon-linux-x64.deb` | `Flocon-linux-x64.rpm` |
| arm64 (aarch64) | `Flocon-linux-arm64.deb` | `Flocon-linux-arm64.rpm` |

A portable jar is also attached (`Flocon-linux-x64.jar`, `Flocon-linux-arm64.jar`); it needs a JDK 21 to run.

## Debian / Ubuntu

```bash
sudo apt install ./Flocon-linux-x64.deb      # or Flocon-linux-arm64.deb
```

## Fedora / RHEL / openSUSE

```bash
sudo dnf install ./Flocon-linux-x64.rpm      # or Flocon-linux-arm64.rpm
```

## Arch Linux (AUR)

Flocon is packaged on the AUR as [`flocon-bin`](https://aur.archlinux.org/packages/flocon-bin) (x86_64 and aarch64). It repackages the release `.deb`, which bundles its own Java runtime.

```bash
yay -S flocon-bin        # or paru -S flocon-bin
```

`android-tools` (adb) is an optional dependency: install it for screenshots, screen recordings and deeplinks.

```bash
sudo pacman -S android-tools
```

### Maintainer setup (one time)

The package is updated automatically on each published release by `.github/workflows/aur.yml`. It needs a manual setup:

1. Create an account on [aur.archlinux.org](https://aur.archlinux.org).
2. Generate a dedicated key: `ssh-keygen -t ed25519 -C "flocon-aur" -f aur_key` (no passphrase), and add `aur_key.pub` in your AUR account (My Account, SSH Public Key).
3. Add the private key `aur_key` as the GitHub repository secret `AUR_SSH_PRIVATE_KEY`, then delete it locally.
4. Create the package once: `git clone ssh://aur@aur.archlinux.org/flocon-bin.git`, copy `packaging/aur/flocon-bin/PKGBUILD` and `.SRCINFO` in it, set `pkgver` to a published release, run `updpkgsums && makepkg --printsrcinfo > .SRCINFO`, commit and push. The workflow can then take over.
5. Without the secret the workflow only logs a warning and skips. It can also be run by hand (Actions, "Publish to AUR", with a version).

## adb

Flocon uses `adb` for Android screenshots, screen recordings and deeplinks. Install it separately:

```bash
sudo apt install android-tools-adb   # Debian / Ubuntu
sudo dnf install android-tools       # Fedora
```

Flocon starts from the app menu (Development) or with `/opt/flocon/bin/Flocon`.
