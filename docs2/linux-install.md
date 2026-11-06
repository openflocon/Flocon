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

A Flatpak bundle is also available, see [Flatpak Installation](flatpak-install.md).

## Debian / Ubuntu

```bash
sudo apt install ./Flocon-linux-x64.deb      # or Flocon-linux-arm64.deb
```

## Fedora / RHEL / openSUSE

```bash
sudo dnf install ./Flocon-linux-x64.rpm      # or Flocon-linux-arm64.rpm
```

## adb

Flocon uses `adb` for Android screenshots, screen recordings and deeplinks. Install it separately:

```bash
sudo apt install android-tools-adb   # Debian / Ubuntu
sudo dnf install android-tools       # Fedora
```

Flocon starts from the app menu (Development) or with `/opt/flocon/bin/Flocon`.
