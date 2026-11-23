# Proxy mode: design, status and next steps

> **For agents picking this up:** read this file first. It is the handoff for PR [#882](https://github.com/openflocon/Flocon/pull/882) (branch `feat/network-proxy-mode`, draft). [What is left](#what-is-left) is the remaining work. The user docs are in `docs/proxy.md`, the proxy has its own `proxy/README.md`.

## Intent

Show the HTTP(S) traffic of a phone in Flocon Desktop **without the Flocon SDK in the app**. The phone sets Flocon's computer as its Wi-Fi proxy, and Flocon Desktop runs a MITM proxy. This also covers what the SDK can't see: WebViews, third-party SDKs and other apps.

The in-app SDK stays the main mode. The proxy is a complement: it has no app context, needs a CA on the phone, and fails on apps that pin certificates or don't trust user CAs.

## Decisions (agreed with the user)

1. **The proxy is a separate TypeScript program, not Kotlin code.** It is built on [mockttp](https://github.com/httptoolkit/mockttp) (HTTP Toolkit): CA generation, MITM, HTTP/2 and WebSockets are already done. Picked over mitmproxy (Python, heavier) and a Netty + BouncyCastle MITM in Kotlin (weeks of work).
2. **It runs with the Node.js of the computer; without Node, Flocon downloads a standalone binary.** The installers only ship a ~5 MB JS bundle (they grow by ~2 MB, against ~100 MB per platform for an embedded binary). Most developers have Node. *(First version embedded the Bun-compiled binary in every installer; changed after review.)*
3. **The proxy talks the regular Flocon protocol.** It connects to the desktop websocket like any SDK (`bonjour`, then `network` messages), so the whole desktop pipeline (domain, data, Room, Network screen, MCP) works unchanged. No new protocol plugin, no `Protocol.kt` change.
4. **One "device" per phone, kept apart from SDK devices.** Proxy traffic is **not** merged into the device that runs the SDK (matching by IP breaks on the simulator and with `adb reverse`, and merging would show every call twice). Each phone is `Proxy <IP>`, in its own **PROXY** group of the device selector, with a router icon.
5. **The setup steps live in the desktop dialog**, not on the phone: the phone only opens the certificate page for a few seconds.

## How it works

```
phone ──(Wi-Fi proxy :8080)──▶ flocon-proxy (mockttp, MITM with the Flocon CA) ──▶ internet
                                   │
                                   └──(wss://127.0.0.1:<ws port>/, Flocon protocol)──▶ Flocon Desktop ServerJvm
phone ──(http://<computer ip>:8081/)──▶ certificate page served by flocon-proxy
Flocon Desktop ProxyManager ──starts / watches / kills──▶ node flocon-proxy.mjs   (or the downloaded binary)
```

### Runtime (`ProxyRuntime.kt`)

1. **Node 18+ found** (PATH, then `/opt/homebrew/bin`, `/usr/local/bin`, `/usr/bin`, Volta, fnm, the newest nvm; on Windows Program Files, LocalAppData, `NVM_SYMLINK`, Volta. An app started from the Dock/Start menu does not get the shell PATH): the bundle `resources/proxy/flocon-proxy.mjs` is copied out of the app into `~/.flocon/proxy/<version>/` (Node can't read inside a jar) and run with `node`.
2. **No Node**: `flocon-proxy-<platform>[.exe]` already in `~/.flocon/proxy/<version>/`, else downloaded from `https://github.com/openflocon/Flocon/releases/download/<BuildConfig.APP_VERSION>/`, checked against its `.sha256`, made executable. `ProxyStateDomainModel.Downloading(progress)` drives a progress bar in the dialog. A file fetched by the app is not quarantined by macOS.

Platforms: `macos-arm64`, `macos-x64`, `windows-x64`, `linux-x64`, `linux-arm64` (`currentPlatformFolder()`).

### Wire format sent by the proxy

See `internal-docs/protocol/README.md` § "Proxy client" and `internal-docs/protocol/network.md`.

| Envelope field | Value |
|---|---|
| `deviceId` | `proxy-<phone IP>` (IP from mockttp `remoteIpAddress`, `::ffff:` stripped) |
| `deviceName` | `Proxy <phone IP>` |
| `appName` / `appPackageName` | `Proxy` / `flocon.proxy` |
| `appInstance` | start time of the proxy process |
| `platform` | `proxy`. Not `android` or `ios`, so the platform features (screenshots, adb…) stay off |
| `bonjour` body | `{"plugins":["network"],"connectionType":"wifi","sdk":"proxy"}`. Keep `wifi`: with `default`, `supportsAdb()` would be true |

- `bonjour` is sent once per phone IP and per websocket session, before that phone's first message.
- A request → `log_network_call_request` with `floconCallId` = mockttp request id. Its response → `log_network_call_response`. An abort → a response with `responseError: "Aborted"`.
- Bodies are decoded (gzip/br/deflate) and truncated at 2 MB. Image bodies are not sent (`isImage: true`). Under Bun, mockttp sometimes hands out its in-progress body (`asBuffer`/`asDecodedBuffer`) instead of the completed one (`buffer`/`getDecodedBuffer`): `readBody` reads both.
- The websocket to the desktop uses `ws` with `rejectUnauthorized: false` (the desktop certificate is self-signed). Same code under Node and Bun.
- **Connections to this computer** (`localhost`, `127.0.0.1`, its IPv4s) are passed through without TLS interception (`tlsPassthrough`) and not reported: they are the SDK of the app talking to Flocon, which would otherwise fail (the proxy refused the self-signed desktop certificate, 502).
- Desktop → mobile messages (mocks, bad network) are ignored.

### Process contract (proxy ⇄ ProxyManager)

Arguments: `--port <proxy> --ca-port <ca page> --flocon-ws wss://127.0.0.1:<ws port>/ --locale <tag> --ca-dir <dir>`.

stdout, one JSON object per line (`parseProxyEvent`):
- `{"event":"ready","proxyPort":8080,"caPort":8081,"ips":["192.168.1.12"]}`
- `{"event":"error","message":"…"}`
- `{"event":"tls-error","host":"api.example.com","reason":"cert-rejected"}`: the CA is not trusted on the phone, or the app pins its certificates. Only logged today.

stderr goes to `~/.flocon/flocon-proxy.log`. The proxy stops on SIGTERM/SIGINT and when its stdin closes (Flocon died). The CA (`flocon-ca.key` / `flocon-ca.pem`) is kept in `~/.flocon/proxy-ca/`, so a phone installs it once. `ProxyManager` restarts a crashed proxy with a backoff (1 s → 30 s), never runs two processes (a mutex around start/stop), and kills it when Flocon quits.

`RunProxyUseCase` (collected in `App()`) starts the proxy at launch if it was on, and restarts it when the websocket port or the language of Flocon changes.

### Certificate page

`http://<computer ip>:<ca port>/`: Flocon logo (inlined in `proxy/src/logo.ts`, base64: Bun's `dataurl`/`base64` loaders produced an empty string), one big button for the platform of the phone (user agent), a link for the other one. Language: `--locale` (the language chosen in Flocon, else the computer's); texts in English and French in `index.ts`, English otherwise. `/ca.crt` is DER **served as a download** (Android 11+ refuses a CA opened from the browser: it must be installed from Settings), `/ca.mobileconfig` the iOS profile (`com.apple.security.root`), `/ca.pem` PEM.

There is no deeplink to the Android "Install a CA certificate" screen from a web page (Chrome only opens BROWSABLE activities, Settings are not), and since Android 11 apps can't install a CA.

### Desktop UI

- **Top bar**: router button → `ProxyDialog`.
- **`ProxyDialog`**: a step-by-step assistant, same size on or off.
  - Header: title, EXPERIMENTAL, "● Running", close. Behind the title, `ProxyCodeRain` (mirrored katakana, green; red on failure); off, a cursor types "Wake up…" (`ProxyWakeUpText`) instead.
  - Stepper Wi-Fi / Certificate / Your app (clickable), Android / iOS toggle.
  - Off: "The proxy is off" + a big "Activate the proxy" button; steps and footer dimmed.
  - **Wi-Fi**: a drawn phone on its proxy settings (platform labels, Manual checked, server and port highlighted) + 3 steps + copy chips + a dropdown of the computer's networks with their interface name (link-local 169.254.x.x left out).
  - **Certificate**: QR code to the page + steps with lettered sub-steps (Android 2 steps, iOS 3) + the page address on a dark block.
  - **Your app**: Android, the `network_security_config` snippet with copy; iOS, nothing more to do.
  - Footer: Disable, Back, Next / Done.
- **Mascot** (`ProxyMascotPeek`, shares `FloconMascotDialog` with Connect): running, long black coat and dark shades catching the code, with an idle loop (glint on the shades, ear, glance); off, office shirt and tie, asleep and breathing with a rising "z" ("not the chosen one yet"); failed, shades slipped, red glints. Mascot and rain follow the mascot setting. Design canvas: https://claude.ai/artifact/Gg8TFMz45PxrmHMH316X8U.
- Strings in English and French (other languages fall back to English).

## Files

| Path | Role |
|---|---|
| `proxy/src/index.ts`, `proxy/src/logo.ts` | The proxy: CA, mockttp passthrough, websocket client, certificate page, stdout events |
| `proxy/package.json`, `proxy/bun.lock` | `mockttp`, `ws`; scripts `dev`, `dev:node`, `build:node`, `build:<platform>` |
| `proxy/README.md` | How to run and build |
| `composeApp/src/desktopMain/resources/proxy/flocon-proxy.mjs` | The Node bundle, **git-ignored**, built by `bun run build:node` (needed before `./gradlew :composeApp:run`) |
| `composeApp/src/desktopMain/…/features/proxy/` | `ProxyManager` (process), `ProxyRuntime` (Node / download), `ProxyEvent` (stdout parsing), `ProxyRepositoryImpl`, `ProxyPlatformModule.desktop.kt` (Koin) |
| `composeApp/src/commonMain/…/features/proxy/` | `ProxyViewModel`, `mapper/`, `model/` (`ProxyMoodUiModel`…), `view/` (`ProxyDialog`, `ProxyMascotPeek`, `ProxyCodeRain`, `ProxyWakeUpText`), `DI.kt` |
| `domain/…/proxy/` | `ProxyStateDomainModel` (Stopped, Starting, Downloading, Running, Failed), `ProxyRepository`, `RunProxyUseCase`, `Observe/SetProxyEnabledUseCase`, `ObserveProxyStateUseCase` |
| `library/designsystem/…/FloconMascotDialog.kt` | Dialog with the mascot on its top edge (Connect and Proxy) |
| `library/designsystem/…/FloconPixelPalette.kt` | Coat, shades, neon, shirt colors |
| `library/designsystem/…/FloconSegmentedIconToggle.kt` | Text segments sized on their measured text |
| `composeApp/…/app/ui/…` (`DeviceItemUiModel`, `Mapper`, `DevicesStateUiModel`, `DevicePlatform`, `TopBarApp`, `TopBarConnectButton`) | `Platform.Proxy`, PROXY group, router icon, proxy button |
| `.github/workflows/release.yml` | Bun 1.4.5, `build:node` + `build:<platform>` per runner, binaries + `.sha256` uploaded as release assets |
| Tests | `ProxyEventTest`, `ProxyRuntimeTest`, `ProxyMapperTest`, `DevicePlatformMapperTest`, `RunProxyUseCaseTest` |
| `docs/proxy.md`, `internal-docs/protocol/README.md` | User docs (experimental), § "Proxy client" |

## Status

Done and checked:
- Mac: the proxy under Bun, under Node, and the compiled binary: HTTP, HTTPS (with the Flocon CA), gzip and POST bodies land in Flocon under `proxy-<ip>`; SIGTERM stops it in ~0.5 s.
- App: on/off, persisted, restart at launch; Node found and used (`node ~/.flocon/proxy/<version>/flocon-proxy.mjs`); every dialog state; mascot and rain; certificate page in French/English.
- **Real Android phones**: Samsung on the same Wi-Fi (CA installed from Settings, calls captured); Xiaomi on another network through USB (`adb reverse tcp:8080 tcp:8080` and `tcp:8081`, Wi-Fi proxy `127.0.0.1:8080`, device `Proxy 127.0.0.1`). The SDK of an app keeps reaching Flocon with the proxy on.
- FloconKotlin sample: trusts user CAs in debug, PR [openflocon/FloconKotlin#6](https://github.com/openflocon/FloconKotlin/pull/6) (draft). Without it, its HTTPS calls fail with `CertPathValidatorException`.
- Release: a draft run (`99.0.0`, since deleted) built the 5 platforms and published the binaries with valid SHA-256 files; installers grew by ~2 MB.
- CI green on the PR; unit tests and ktlint pass.

## What is left

Follow the repo rules: layering in `.claude/rules/desktop-app.md`, UI in `.claude/rules/compose.md`, docs sync in `.claude/rules/docs-protocol-sync.md`.

- [ ] **Test the download path for real**: needs a published release (or pre-release) with the assets, then a computer without Node. Draft assets are not reachable at `releases/download/…`. In dev the version is `2.0.0`, which has no assets: without Node the dialog shows the download error.
- [ ] **Test on an iPhone** (profile install, Certificate Trust Settings, calls captured).
- [ ] **USB mode in the dialog**: when an Android phone is plugged in, set up `adb reverse` for the proxy and CA ports and tell the user to use `127.0.0.1:8080` (works when the phone is not on the computer's network). Could also `adb push` the CA to Downloads and open the Security settings (`am start -a android.settings.SECURITY_SETTINGS`), leaving only "Install a certificate › CA certificate".
- [ ] **Show the hosts that refuse the CA** (`tls-error` events) in the dialog: "these apps pin their certificates or don't trust user CAs".
- [ ] **Merge FloconKotlin#6** (sample trusts user CAs in debug).
- [ ] **Docs**: screenshots of the dialog in `docs/proxy.md` (`flocon-mock-mode` skill); mention the USB mode once it exists.
- [ ] **Translations**: the dialog strings exist in English and French only; the certificate page too (`PAGE_TEXTS_BY_LANGUAGE` in `index.ts`).
- [ ] **Signing**: the app is not signed or notarized. If macOS signing is added, check what Gatekeeper does with the downloaded binary.
- [ ] Linux: the katakana of the rain need a CJK font, else they show as boxes.
- [ ] Later / optional: apply Flocon mocks and bad-network settings to proxied calls (read `setup_mocks` / `setup_bad_network_config`, answer with mockttp rules); WebSocket frames as `log_websocket_event`; a "Proxy" filter or badge in the Network list; network type labels (Wi-Fi / VPN) instead of interface names in the dropdown.
