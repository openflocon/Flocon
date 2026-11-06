---
title: Flatpak Installation
description: Install Flocon Desktop as a Flatpak on Linux (x86_64 and aarch64), and how adb works in the sandbox.
---

# Flatpak Installation

Flocon Desktop is packaged as a Flatpak (`io.github.openflocon.flocon`) for x86_64 and aarch64. Each release has a `.flatpak` bundle attached:

| Architecture | Bundle |
|---|---|
| x86_64 | `Flocon-linux-x86_64.flatpak` |
| aarch64 | `Flocon-linux-aarch64.flatpak` |

```bash
flatpak install --user ./Flocon-linux-x86_64.flatpak   # or Flocon-linux-aarch64.flatpak
flatpak run io.github.openflocon.flocon
```

The bundle needs the `org.freedesktop.Platform` runtime from Flathub (`flatpak remote-add --if-not-exists flathub https://dl.flathub.org/repo/flathub.flatpakrepo`).

## How it is built

The Flatpak repackages the `.deb` of the release (`Flocon-linux-x64.deb` / `Flocon-linux-arm64.deb`). That package is a jpackage app image and already contains its own Java runtime, so the manifest only needs `org.freedesktop.Platform` and no JDK extension (`org.freedesktop.Sdk.Extension.openjdk21`). The trade-off is a larger download (the JRE is bundled), in exchange for the exact same build as the other Linux packages and no Gradle build inside Flatpak (which would need pinned offline sources for every Kotlin and Maven dependency). The sha256 of each `.deb` is pinned in the manifest by `packaging/flatpak/render-manifest.sh`.

## Permissions

| Permission | Why | Cost |
|---|---|---|
| `--share=network` | The mobile SDK connects to Flocon over the network, and adb can reach devices over TCP. It also makes the host `adb` server on `localhost:5037` reachable. | Full network access. Required. |
| `--socket=x11`, `--socket=wayland`, `--share=ipc`, `--device=dri` | Window and GPU rendering. | Standard for a desktop app. |
| `--device=all` | USB debugging with the bundled adb (needs access to `/dev/bus/usb`). | Broad: exposes all host devices. Drop it with `flatpak override --user --nodevice=all io.github.openflocon.flocon` if you use the host adb server instead. |
| `--filesystem=xdg-documents/Flocon:create` | Default Flocon folder `~/Documents/Flocon` (screenshots, recordings). | Only that sub-folder is shared, not all your documents. If you change the folder in Settings, grant it with `flatpak override --user --filesystem=/your/path io.github.openflocon.flocon`. |
| `--filesystem=~/.android:create` | Share adb keys with the host so authorized devices stay authorized. | Exposes `~/.android` (adb keys, AVDs). |

No `--talk-name=org.freedesktop.Flatpak` (which would allow `flatpak-spawn --host`): it is a sandbox escape and Flathub rejects it.

## adb

Flocon needs `adb` for Android screenshots, screen recordings and deeplinks.

- **x86_64**: `adb` (Android platform-tools, pinned version) is bundled in the Flatpak and is in the `PATH`, so Flocon finds it with no setup. For USB devices, the host needs the usual udev rules for Android devices.
- **aarch64**: Google does not publish platform-tools for Linux arm64, so nothing is bundled. Flocon runs, and the SDK connection works over the network, but features based on adb (screenshots, recordings, deeplinks) are unavailable in the Flatpak. Use the [`.deb` / `.rpm`](linux-install.md) on arm64 if you need them.

### Version mismatch with the host adb

Because the network is shared with the host, the bundled `adb` talks to a host `adb` server if one is already running on port 5037. If the two versions differ, adb kills and restarts the server, which can disconnect your other tools. Keep one adb at a time: either stop the host server (`adb kill-server`) before using Flocon, or use the host adb server only (remove `--device=all` and run `adb start-server` on the host).

### `flatpak-spawn --host adb`

Running the host adb from inside the sandbox needs `--talk-name=org.freedesktop.Flatpak`, which is intentionally not granted. If you accept that risk for a local install, you can add it yourself with `flatpak override --user --talk-name=org.freedesktop.Flatpak io.github.openflocon.flocon`, create a wrapper script that runs `flatpak-spawn --host adb "$@"`, and set its path in Flocon Settings. This is not supported and not recommended.

## Build it locally

```bash
packaging/flatpak/render-manifest.sh 1.9.6 ./assets ./build-flatpak   # assets holds the two .deb of the release
cd build-flatpak
flatpak-builder --user --install --force-clean build io.github.openflocon.flocon.yml
```

## Releases (CI)

`.github/workflows/flatpak.yml` runs after "Desktop - Build and Release" succeeds on a published release. For each architecture it downloads the `.deb`, pins their sha256, validates the `.desktop` and AppStream files, builds with `flatpak-builder`, and attaches the `.flatpak` bundle to the release. It can also be run by hand (`workflow_dispatch`) with a release tag.

## Submitting to Flathub (checklist)

1. Add real screenshots to the metainfo (`<screenshots>`, stable HTTPS URLs; Flathub requires at least one) and a 256x256 or SVG icon (the current icon is 250x250).
2. Pass `appstreamcli validate --pedantic` and `flatpak-builder-lint manifest` / `flatpak-builder-lint appstream` on the rendered files.
3. Prepare the submission with the rendered manifest from the `flatpak-manifest` CI artifact (real version and sha256).
4. Fork [flathub/flathub](https://github.com/flathub/flathub), create a branch from `new-pr` and add the manifest, `.desktop`, metainfo and icon (the manifest file name must be `io.github.openflocon.flocon.yml`). Open the PR against the `new-pr` branch.
5. App id: `io.github.openflocon.flocon` matches the GitHub namespace `openflocon`; the author must be able to prove ownership of it (verification on Flathub).
6. Expect review comments on the permissions: justify `--device=all` (USB adb) and the `~/.android` access, or reduce them. Remote binary sources (the `.deb`) are accepted, but Flathub builds on its own infrastructure, so every `url` needs a pinned sha256.
7. Once merged, Flathub creates `flathub/io.github.openflocon.flocon`. Each release then needs a PR to that repo (or enable `flathub-infra/flatpak-external-data-checker` to bump the URLs and sha256 automatically).
8. Then this page can recommend `flatpak install flathub io.github.openflocon.flocon` and the CI bundle stays as an alternative.
