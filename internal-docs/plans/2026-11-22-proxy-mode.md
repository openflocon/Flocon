# Proxy mode: design, status and next steps

> **For agents picking this up:** read this file first. It is the handoff for PR [#882](https://github.com/openflocon/Flocon/pull/882) (branch `feat/network-proxy-mode`, draft). The "Next steps" checklist is the remaining work, in order. The user docs are in `docs/proxy.md`. The proxy binary has its own `proxy/README.md`.

## Intent

Show the HTTP(S) traffic of a phone in Flocon Desktop **without the Flocon SDK in the app**. The phone sets Flocon's computer as its Wi-Fi proxy, and Flocon Desktop runs a MITM proxy. This also covers what the SDK can't see: WebViews, third-party SDKs and other apps.

The in-app SDK stays the main mode. The proxy is a complement: it has no app context, needs a CA on the phone, and fails on apps that pin certificates.

## Decisions (already agreed with the user)

1. **The proxy is an external binary, not Kotlin code.** It is written in TypeScript on [mockttp](https://github.com/httptoolkit/mockttp), the HTTP Toolkit lib: CA generation, MITM, HTTP/2 and WebSockets are already done. It is compiled with `bun build --compile` into one standalone binary per OS. We picked it over mitmproxy (Python, heavier binary) and over writing a Netty + BouncyCastle MITM in Kotlin (weeks of work).
2. **It is shipped inside the app** (Compose Desktop `appResourcesRootDir`, option "embed"), not downloaded on demand. It works offline and is signed with the app bundle.
3. **The proxy talks the regular Flocon protocol.** It connects to the desktop websocket like any SDK (`bonjour`, then `network` messages), so the whole desktop pipeline (domain, data, Room, Network screen, MCP) works unchanged. There is no new protocol plugin and no `Protocol.kt` change.
4. **One "device" per phone, kept apart from SDK devices.** Proxy traffic is **not** merged into the device that runs the SDK. Matching by IP breaks on the simulator and with `adb reverse` (everything is `127.0.0.1`), and merging would show every call twice. Each phone shows up as `Proxy <IP>`, in its own **PROXY** group of the device selector, with a router icon.

## How it works

```
phone ──(Wi-Fi proxy :8080)──▶ flocon-proxy (mockttp, MITM with the Flocon CA) ──▶ internet
                                   │
                                   └──(wss://127.0.0.1:<ws port>/, Flocon protocol)──▶ Flocon Desktop ServerJvm
phone ──(http://<mac ip>:8081/)──▶ CA download page served by flocon-proxy
Flocon Desktop ProxyManager ──starts / watches / kills──▶ flocon-proxy process (stdout = JSON events)
```

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

- `bonjour` is sent once per phone IP and per websocket session, before that phone's first message (`greetedDevices` is cleared on reconnect).
- A request → `log_network_call_request` with `floconCallId` = mockttp request id. Its response → `log_network_call_response` with the same id. An abort → a response with `responseError: "Aborted"`.
- Bodies are decoded (gzip/br/deflate) and truncated at 2 MB. Image bodies are not sent (`isImage: true`).
- The desktop websocket is TLS with a self-signed certificate, so the proxy opens it with `{ tls: { rejectUnauthorized: false } }`, a Bun extension of the `WebSocket` constructor.
- Desktop → mobile messages (mocks, bad network) are ignored.

### Process contract (proxy ⇄ ProxyManager)

Arguments: `--port <proxy> --ca-port <ca page> --flocon-ws wss://127.0.0.1:<ws port>/ --ca-dir <dir>`.

stdout, one JSON object per line, parsed by `parseProxyEvent`:
- `{"event":"ready","proxyPort":8080,"caPort":8081,"ips":["192.168.1.12"]}`
- `{"event":"error","message":"…"}`
- `{"event":"tls-error","host":"api.example.com","reason":"cert-rejected"}`: usually the CA is not trusted on the phone, or the app pins certificates.

stderr is appended to `<dataDir>/flocon-proxy.log`. The proxy stops on SIGTERM/SIGINT, and when its stdin closes (Flocon died). The CA (`flocon-ca.key` / `flocon-ca.pem`) is kept in `<dataDir>/proxy-ca/` so the phone installs it only once.

CA page (`http://<mac ip>:<ca port>/`): `/` is an HTML page with instructions, `/ca.mobileconfig` is the iOS profile (`com.apple.security.root`), `/ca.crt` is DER for Android, `/ca.pem` is PEM.

## Files

| Path | Role |
|---|---|
| `proxy/src/index.ts` | The proxy: CA, mockttp passthrough, Flocon websocket client, CA page, stdout events |
| `proxy/package.json` | `mockttp` dependency; `dev` and `build:<platform>` / `build:all` scripts (output in `composeApp/appResources/<platform>/`) |
| `proxy/README.md` | How to run and build the binary |
| `composeApp/appResources/` | Native app resources (`common/`, `macos-arm64/`, `macos-x64/`, `windows-x64/`, `linux-x64/`). The binaries are git-ignored |
| `composeApp/build.gradle.kts` | `nativeDistributions { appResourcesRootDir.set(…"appResources") }` |
| `composeApp/src/desktopMain/…/features/proxy/ProxyManager.kt` | Starts the binary (from `compose.application.resources.dir`, or `appResources/<platform>/` in dev), picks free ports, maps stdout to `StateFlow<ProxyState>`, restarts with a backoff (1 s → 30 s, reset after a 30 s run), kills the process on stop / app exit |
| `composeApp/src/desktopMain/…/features/proxy/ProxyEvent.kt` | `ProxyState` (`Stopped`, `Starting`, `Running(proxyPort, caPort, ips)` with `caUrl`, `Failed`), `ProxyEvent`, `parseProxyEvent` |
| `composeApp/src/desktopTest/…/features/proxy/ProxyEventTest.kt`, `ProxyMapperTest.kt`, `domain/…/proxy/RunProxyUseCaseTest.kt` | Parsing, platform folder, UI mapping (`caUrl`), start/stop/restart rules |
| `composeApp/src/commonMain/…/app/ui/model/DeviceItemUiModel.kt` | `Platform.Proxy` |
| `composeApp/src/commonMain/…/app/ui/delegates/Mapper.kt` | `"proxy"` → `Platform.Proxy` |
| `composeApp/src/commonMain/…/app/ui/model/DevicesStateUiModel.kt` | Group order: Android, iOS, Desktop, **Proxy**, Unknown |
| `composeApp/src/commonMain/…/app/ui/view/topbar/device/DevicePlatform.kt` | Router icon, `catWebView` color, `device_platform_proxy` label |
| `composeApp/src/commonMain/…/app/ui/view/topbar/app/TopBarApp.kt` | Router icon when the app has no icon |
| `composeApp/src/commonMain/composeResources/values*/strings.xml` | `device_platform_proxy` (`PROXY`, `代理` in Chinese) |
| `composeApp/src/desktopTest/…/app/ui/delegates/DevicePlatformMapperTest.kt` | Proxy mapping and group order |
| `domain/…/proxy/` | `ProxyStateDomainModel`, `ProxyRepository`, `RunProxyUseCase` (follows on/off + websocket port), `Observe/SetProxyEnabledUseCase`, `ObserveProxyStateUseCase` |
| `composeApp/src/desktopMain/…/features/proxy/ProxyRepositoryImpl.kt`, `ProxyPlatformModule.desktop.kt` | Repository over `ProxyManager` + `SettingsDataSource.proxyEnabled`; Koin (`actual val proxyPlatformModule`) |
| `composeApp/src/commonMain/…/features/proxy/` | `ProxyViewModel`, `mapper/`, `model/`, `view/ProxyDialog.kt`; opened by `TopBarProxyButton` |
| `docs/proxy.md` (+ `mkdocs.yml` nav, under Network) | User docs, marked experimental |
| `internal-docs/protocol/README.md` | § "Proxy client" |

## Status

- Done: everything in the table above. CI (`Desktop - Pull Request Check`) is green on `a1ae8f6`.
- **Never run for real.** The session that wrote it had no Bun, npm or Gradle access. The proxy, the `ProxyManager` process handling and the selector UI have only been checked by CI (compile + unit tests + ktlint). The first thing to do on a Mac is the manual test below.
- Wired (second session): `ProxyManager` behind `domain/proxy` (`ProxyRepository`, `RunProxyUseCase` collected in `App()`), on/off saved in the settings (`proxy_enabled`), and `ProxyDialog` (router button in the top bar) that guides the phone: Wi-Fi proxy server/port, QR code to the CA page, iOS/Android steps. Strings in English and French only (the other languages fall back to English).
- Still **never run with the real binary** (no Bun on the machine). Without the binary the dialog shows "not included in this build".

## Manual test (do this first)

```sh
# 1. Flocon Desktop running (./gradlew :composeApp:run), default ws port 9023
cd proxy && bun install && bun run dev      # proxy on :8080, CA page on :8081
# 2. Quick check from the Mac
curl -x http://127.0.0.1:8080 http://example.com          # must appear in Flocon as device "Proxy 127.0.0.1"
curl -x http://127.0.0.1:8080 --cacert .ca/flocon-ca.pem https://example.com
# 3. Real iPhone on the same Wi-Fi: Wi-Fi proxy manual = <mac ip>:8080,
#    Safari → http://<mac ip>:8081/ → install profile → trust it in Certificate Trust Settings
```

Things to check while testing:
- mockttp API names used in `index.ts` against the installed version (`generateCACertificate`, `getLocal({ https, http2 })`, `forAnyWebSocket`, events `request` / `response` / `abort` / `tls-client-error`, `body.getDecodedBuffer()`, `timingEvents.startTime`). Fix any that changed.
- Bun accepts `new WebSocket(url, { tls: { rejectUnauthorized: false } })` against the desktop self-signed certificate.
- The phone's own SDK websocket to the desktop (if the app has the SDK) may go through the proxy. If it shows up as noise, skip hosts/ports of Flocon in the proxy (`forAnyRequest` → don't report requests to the Flocon ports).
- `bun build --compile` output size per platform, and that the binary starts from `composeApp/appResources/<platform>/` with `./gradlew :composeApp:run`. Check that the `run` task sets `compose.application.resources.dir`, otherwise fix the dev fallback path in `ProxyManager.resolveBinary()`.

## Next steps

Follow the repo rules: layering in `.claude/rules/desktop-app.md`, UI in `.claude/rules/compose.md`, docs sync in `.claude/rules/docs-protocol-sync.md`.

- [ ] **Manual test** above; fix `proxy/src/index.ts` from the results.
- [x] **Wire `ProxyManager`**: register it in a Koin module (desktop source set), with `floconWebsocketUrl = "wss://127.0.0.1:${ports.websocketPort}/"` from `ObserveServerPortsUseCase` (restart the proxy when the port changes) and `dataDir` = the Flocon data dir (`~/.flocon/` is already used by `ServerJvm`). If the UI is in `commonMain`, put an interface (e.g. `ProxyController` with `state` / `start` / `stop`) where commonMain can see it, and the JVM implementation in desktopMain. Ask the `architect` agent if it touches `domain`.
- [x] **UI**: a toggle to start/stop the proxy. Suggested place: next to the existing Connect dialog (`features/connect/ConnectDialog.kt`, which already has `QrCodeImage`) or in Settings. When `Running`, show `<ip>:<proxyPort>` for the Wi-Fi proxy setting and a QR code to `caUrl`, with the iOS / Android steps (see `docs/proxy.md`). Show `Failed.message`. Optionally list `tls-error` hosts ("the app may pin its certificates").
- [x] **Persist** the on/off choice (DataStore settings like the others) and restart the proxy at launch if it was on.
- [ ] **Release** (`.github/workflows/release.yml`): before `packageDistributionForCurrentOS`, set up Bun, run `bun install && bun run build:<platform>` in `proxy/` for the runner's OS/arch. On macOS, make sure the binary is signed with the app (hardened runtime) and passes notarization. `packageUberJarForCurrentOS` does not include `appResources`, so in the uber jar the proxy is unavailable: `ProxyState.Failed("binary not found")` is the expected behavior, show it clearly.
- [ ] **Docs**: drop the "Work in progress" warning in `docs/proxy.md` once the UI exists, and add screenshots (`flocon-mock-mode` skill).
- [ ] Later / optional: apply Flocon mocks and bad-network settings to proxied calls (read `setup_mocks` / `setup_bad_network_config` from the websocket, answer with mockttp rules); WebSocket frames through the proxy as `log_websocket_event`; a "Proxy" filter or badge in the Network list.
