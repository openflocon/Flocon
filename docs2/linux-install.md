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

## Homebrew (Linuxbrew)

If you use [Homebrew on Linux](https://docs.brew.sh/Homebrew-on-Linux), install the portable jar with its launcher (x64 and arm64). Homebrew installs OpenJDK 21 for you:

```bash
brew install openflocon/tap/flocon
flocon
```

Update it later with `brew upgrade flocon`. Homebrew does not install `adb`: see [adb](#adb) below.

## Debian / Ubuntu

```bash
sudo apt install ./Flocon-linux-x64.deb      # or Flocon-linux-arm64.deb
```

## Fedora / RHEL

```bash
sudo dnf install ./Flocon-linux-x64.rpm      # or Flocon-linux-arm64.rpm
```

## openSUSE

```bash
sudo zypper install ./Flocon-linux-x64.rpm   # or Flocon-linux-arm64.rpm
```

## adb

Flocon uses `adb` for Android screenshots, screen recordings and deeplinks. Install it separately:

```bash
sudo apt install android-tools-adb   # Debian / Ubuntu
sudo dnf install android-tools       # Fedora
sudo zypper install android-tools    # openSUSE
```

Flocon starts from the app menu (Development) or with `/opt/flocon/bin/Flocon`.
