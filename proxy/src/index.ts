// Flocon proxy: an HTTP(S) MITM proxy (mockttp) that reports the traffic it sees to Flocon Desktop.
//
// It speaks the regular Flocon protocol (see internal-docs/protocol/): for Flocon Desktop it is just
// one more client, with one "device" per phone that uses the proxy (keyed by its IP address). So the
// calls show up in the existing Network screen without any change in the desktop pipeline.
//
// Usage:
//   flocon-proxy --port 8080 --ca-port 8081 --flocon-ws wss://127.0.0.1:9023/ --ca-dir <dir>
//
// stdout: one JSON object per line, read by ProxyManager in Flocon Desktop:
//   {"event":"ready","proxyPort":8080,"caPort":8081,"ips":["192.168.1.12"]}
//   {"event":"client","ip":"192.168.1.12"}   a phone not linked to a device yet used the proxy
//   {"event":"error","message":"..."}
//
// stdin: one JSON object per line, sent by Flocon Desktop to link the proxy traffic to the real device
// (the Android phone found with adb) instead of a "Proxy <ip>" device:
//   {"cmd":"devices","ips":{"192.168.1.12":<device>},"ports":{"18100":<device>}}
//   <device> = {"deviceId":"…","deviceName":"…","platform":"android","connectionType":"wifi"}
// connectionType is left out for a device of the SDK: its own connection type is kept (adb stays on).
// "ips": the Wi-Fi addresses of the phones. "ports": one extra proxy port per phone plugged with
// `adb reverse` (all of them reach the proxy from 127.0.0.1, only the port tells them apart).
// Each command replaces the previous one. When stdin closes (Flocon died), the proxy stops.

import * as mockttp from "mockttp";
import { createInterface } from "node:readline";
import { existsSync, mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { join } from "node:path";
import { createServer, type IncomingMessage } from "node:http";
import { randomUUID } from "node:crypto";
import WebSocket from "ws";
import { FLOCON_LOGO } from "./logo";
import { networkInterfaces } from "node:os";

// ---------- Args ----------
function emit(obj: unknown) {
  process.stdout.write(JSON.stringify(obj) + "\n");
}

function arg(name: string, def?: string): string {
  const i = process.argv.indexOf(`--${name}`);
  if (i !== -1 && process.argv[i + 1]) return process.argv[i + 1];
  if (def !== undefined) return def;
  emit({ event: "error", message: `missing --${name}` });
  process.exit(2);
}

const PROXY_PORT = Number(arg("port", "8080"));
const CA_PORT = Number(arg("ca-port", String(PROXY_PORT + 1)));
const FLOCON_WS = arg("flocon-ws", "wss://127.0.0.1:9023/");
const CA_DIR = arg("ca-dir");
const VERSION = "0.1.0";
const APP_INSTANCE = Date.now();
const MAX_BODY = 2 * 1024 * 1024; // bodies above 2 MB are truncated

// ---------- CA (created once, reused across launches) ----------
async function loadOrCreateCA(): Promise<{ key: string; cert: string }> {
  mkdirSync(CA_DIR, { recursive: true });
  const keyPath = join(CA_DIR, "flocon-ca.key");
  const certPath = join(CA_DIR, "flocon-ca.pem");
  if (existsSync(keyPath) && existsSync(certPath)) {
    return { key: readFileSync(keyPath, "utf8"), cert: readFileSync(certPath, "utf8") };
  }
  const ca = await mockttp.generateCACertificate({ commonName: "Flocon Proxy CA", organizationName: "Flocon" });
  writeFileSync(keyPath, ca.key, { mode: 0o600 });
  writeFileSync(certPath, ca.cert);
  return ca;
}

// ---------- Link to Flocon Desktop (queue + reconnect) ----------
const queue: string[] = [];
const greetedDevices = new Set<string>();
let ws: WebSocket | null = null;

function connectFlocon() {
  // The desktop serves a self-signed certificate: clients must accept it without verifying it
  // (see internal-docs/protocol/README.md, Transport). `ws` runs the same under Node and Bun.
  const socket = new WebSocket(FLOCON_WS, { rejectUnauthorized: false });
  ws = socket;
  socket.on("open", () => {
    greetedDevices.clear(); // a new session needs a new handshake for every device
    flushQueue();
  });
  socket.on("close", () => setTimeout(connectFlocon, 1000));
  socket.on("error", () => socket.close());
}

function flushQueue() {
  while (queue.length && ws?.readyState === WebSocket.OPEN) ws.send(queue.shift()!);
}

function sendRaw(s: string) {
  if (ws?.readyState === WebSocket.OPEN) ws.send(s);
  else {
    queue.push(s);
    if (queue.length > 5000) queue.shift();
  }
}

// ---------- Devices: who used the proxy ----------
/** The device a call is shown under in Flocon Desktop. */
// connectionType: sent in the bonjour, null to keep the one of the SDK on the same device
type Device = { deviceId: string; deviceName: string; platform: string; connectionType: string | null };

// filled by the "devices" command of Flocon Desktop
let deviceByIp = new Map<string, Device>();
let deviceByPort = new Map<number, Device>();
const reportedIps = new Set<string>();

/** A phone Flocon Desktop does not know: one "Proxy <ip>" device per IP address. */
function unlinkedDevice(ip: string): Device {
  // "wifi": the adb features stay off for it
  return { deviceId: `proxy-${ip}`, deviceName: `Proxy ${ip}`, platform: "proxy", connectionType: "wifi" };
}

/** The device of a call: by the proxy port for `adb reverse` phones, else by the IP of the phone. */
function deviceOf(ip: string, port: number | null): Device {
  const linked = (port !== null ? deviceByPort.get(port) : undefined) ?? deviceByIp.get(ip);
  if (linked) return linked;
  if (!reportedIps.has(ip)) {
    reportedIps.add(ip);
    emit({ event: "client", ip }); // Flocon Desktop looks for this phone now, not at its next refresh
  }
  return unlinkedDevice(ip);
}

function parseDevices(value: unknown): Map<string, Device> {
  const out = new Map<string, Device>();
  if (!value || typeof value !== "object") return out;
  for (const [key, d] of Object.entries(value as Record<string, any>)) {
    if (typeof d?.deviceId === "string" && typeof d?.deviceName === "string" && typeof d?.platform === "string") {
      const connectionType = typeof d.connectionType === "string" ? d.connectionType : null;
      out.set(key, { deviceId: d.deviceId, deviceName: d.deviceName, platform: d.platform, connectionType });
    }
  }
  return out;
}

/** Sends a Flocon envelope on behalf of `device`. */
function sendToFlocon(device: Device, plugin: string, method: string, body: unknown) {
  const envelope = (p: string, m: string, b: unknown) =>
    JSON.stringify({
      deviceId: device.deviceId,
      deviceName: device.deviceName,
      appName: "Proxy",
      appPackageName: "flocon.proxy",
      appInstance: APP_INSTANCE,
      platform: device.platform,
      versionName: VERSION,
      plugin: p,
      method: m,
      body: JSON.stringify(b),
    });

  if (!greetedDevices.has(device.deviceId) && ws?.readyState === WebSocket.OPEN) {
    greetedDevices.add(device.deviceId);
    // no connection type: the one sent by the SDK of the same phone is kept
    const connection = device.connectionType ? { connectionType: device.connectionType } : {};
    sendRaw(envelope("bonjour", "bonjour", { plugins: ["network"], ...connection, sdk: "proxy" }));
  }
  sendRaw(envelope(plugin, method, body));
}

// ---------- Helpers ----------
// Under Bun, mockttp sometimes hands out its in-progress body (asBuffer / asDecodedBuffer) instead of the
// completed one (buffer / getDecodedBuffer): both shapes are read here.
type AnyBody = Partial<mockttp.CompletedBody> & {
  asBuffer?: () => Promise<Buffer>;
  asDecodedBuffer?: () => Promise<Buffer>;
};

async function rawBuffer(body: AnyBody): Promise<Buffer | undefined> {
  if (body.buffer) return body.buffer;
  return body.asBuffer?.().catch(() => undefined);
}

async function readBody(body: AnyBody | undefined): Promise<{ text: string | null; size: number }> {
  if (!body) return { text: null, size: 0 };
  const raw = await rawBuffer(body);
  const rawSize = raw?.length ?? 0;
  try {
    // handles gzip / br / deflate
    const buf = body.getDecodedBuffer ? await body.getDecodedBuffer() : await body.asDecodedBuffer?.();
    if (!buf) return { text: null, size: rawSize };
    return { text: buf.length ? buf.subarray(0, MAX_BODY).toString("utf8") : null, size: buf.length };
  } catch {
    return { text: null, size: rawSize };
  }
}

async function bodySize(body: AnyBody | undefined): Promise<number> {
  return body ? ((await rawBuffer(body))?.length ?? 0) : 0;
}

function flatHeaders(h: mockttp.Headers): Record<string, string> {
  const out: Record<string, string> = {};
  for (const [k, v] of Object.entries(h)) if (v !== undefined) out[k] = Array.isArray(v) ? v.join(", ") : v;
  return out;
}

function clientIp(addr: string | undefined): string {
  return (addr ?? "unknown").replace(/^::ffff:/, "");
}

function localIPv4s(): string[] {
  return Object.values(networkInterfaces())
    .flat()
    .filter((i) => i && i.family === "IPv4" && !i.internal)
    .map((i) => i!.address);
}

// ---------- CA download server (installed once on the phone) ----------
function pemToBase64Der(pem: string): string {
  return pem.replace(/-----(BEGIN|END) CERTIFICATE-----/g, "").replace(/\s+/g, "");
}

function mobileconfig(certPem: string): string {
  const uuid = () => randomUUID().toUpperCase();
  return `<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0"><dict>
  <key>PayloadContent</key><array><dict>
    <key>PayloadCertificateFileName</key><string>flocon-ca.cer</string>
    <key>PayloadContent</key><data>${pemToBase64Der(certPem)}</data>
    <key>PayloadDisplayName</key><string>Flocon Proxy CA</string>
    <key>PayloadIdentifier</key><string>io.github.openflocon.proxy.ca</string>
    <key>PayloadType</key><string>com.apple.security.root</string>
    <key>PayloadUUID</key><string>${uuid()}</string>
    <key>PayloadVersion</key><integer>1</integer>
  </dict></array>
  <key>PayloadDisplayName</key><string>Flocon Proxy</string>
  <key>PayloadIdentifier</key><string>io.github.openflocon.proxy</string>
  <key>PayloadType</key><string>Configuration</string>
  <key>PayloadUUID</key><string>${uuid()}</string>
  <key>PayloadVersion</key><integer>1</integer>
</dict></plist>`;
}

// ---------- CA page (opened on the phone for a few seconds: the steps are shown in Flocon Desktop) ----------
// The page follows the language of Flocon Desktop (--locale), English when it has no translation.
const PAGE_TEXTS_BY_LANGUAGE = {
  en: {
    title: "Flocon Proxy",
    subtitle: "Install the Flocon certificate so that Flocon Desktop sees the HTTPS calls of this phone.",
    android: "Download the certificate",
    ios: "Install the profile",
    otherAndroid: "Other phone: Download the certificate",
    otherIos: "Other phone: Install the profile",
    next: "Then follow the steps shown in Flocon Desktop.",
    once: "Once per phone.",
  },
  fr: {
    title: "Flocon Proxy",
    subtitle: "Installe le certificat Flocon pour que Flocon Desktop voie les appels HTTPS de ce téléphone.",
    android: "Télécharger le certificat",
    ios: "Installer le profil",
    otherAndroid: "Autre téléphone : Télécharger le certificat",
    otherIos: "Autre téléphone : Installer le profil",
    next: "Puis suis les étapes affichées dans Flocon Desktop.",
    once: "Une seule fois par téléphone.",
  },
};
type PageTexts = (typeof PAGE_TEXTS_BY_LANGUAGE)["en"];

const PAGE_LANGUAGE = arg("locale", "en").toLowerCase().split(/[-_]/)[0];
const PAGE_TEXTS: PageTexts =
  PAGE_TEXTS_BY_LANGUAGE[PAGE_LANGUAGE as keyof typeof PAGE_TEXTS_BY_LANGUAGE] ?? PAGE_TEXTS_BY_LANGUAGE.en;

const escapeHtml = (text: string) =>
  text.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");

const DOWNLOAD_ICON =
  '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 4v11"/><path d="M7 10l5 5 5-5"/><path d="M5 20h14"/></svg>';

function caPage(req: IncomingMessage): string {
  const ua = req.headers["user-agent"] ?? "";
  const t = Object.fromEntries(Object.entries(PAGE_TEXTS).map(([k, v]) => [k, escapeHtml(v)])) as PageTexts;
  // the button of the phone first, the link to the other platform in small below
  const iphone = /iPhone|iPad|iPod/i.test(ua);
  const main = iphone ? { href: "/ca.mobileconfig", label: t.ios } : { href: "/ca.crt", label: t.android };
  const other = iphone ? { href: "/ca.crt", label: t.otherAndroid } : { href: "/ca.mobileconfig", label: t.otherIos };
  return `<!doctype html><html lang="${PAGE_LANGUAGE in PAGE_TEXTS_BY_LANGUAGE ? PAGE_LANGUAGE : "en"}"><head><meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1"><title>${t.title}</title>
<style>
  :root { color-scheme: dark; }
  * { box-sizing: border-box; }
  body { margin: 0; min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 24px;
         background: radial-gradient(120% 70% at 50% 0%, #3A2A0E 0%, #141318 55%, #0D0D10 100%);
         color: #ECECEC; font: 16px/1.45 -apple-system, system-ui, "Segoe UI", Roboto, sans-serif; text-align: center; }
  .card { width: 100%; max-width: 400px; display: flex; flex-direction: column; align-items: center; gap: 18px;
          padding: 32px 24px 24px; border-radius: 28px; background: rgba(27, 27, 31, 0.86);
          border: 1px solid rgba(245, 180, 64, 0.18); box-shadow: 0 24px 60px rgba(0, 0, 0, 0.45); }
  .logo { width: 104px; height: 104px; border-radius: 26px; box-shadow: 0 10px 30px rgba(245, 180, 64, 0.35); }
  h1 { margin: 4px 0 0; font-size: 26px; letter-spacing: -0.01em; }
  .subtitle { margin: 0; color: #B9B9C4; font-size: 15px; }
  .main { display: flex; align-items: center; justify-content: center; gap: 10px; width: 100%; margin-top: 6px;
          padding: 18px 16px; border-radius: 16px; background: #F5B440; color: #1C1405; font-weight: 700; font-size: 17px; white-space: nowrap;
          text-decoration: none; box-shadow: 0 8px 22px rgba(245, 180, 64, 0.35); }
  .main:active { transform: scale(0.98); }
  .next { margin: 0; color: #ECECEC; font-size: 14px; }
  .divider { width: 100%; height: 1px; background: #2A2A2F; }
  .other { color: #F5B440; font-size: 14px; font-weight: 600; text-decoration: none; }
  .once { margin: 0; color: #6B6B7B; font-size: 12px; }
</style></head><body>
  <main class="card">
    <img class="logo" src="${FLOCON_LOGO}" alt="Flocon">
    <h1>${t.title}</h1>
    <p class="subtitle">${t.subtitle}</p>
    <a class="main" href="${main.href}">${DOWNLOAD_ICON}${main.label}</a>
    <p class="next">${t.next}</p>
    <div class="divider"></div>
    <a class="other" href="${other.href}">${other.label}</a>
    <p class="once">${t.once}</p>
  </main>
</body></html>`;
}

function startCAServer(certPem: string) {
  const der = Buffer.from(pemToBase64Der(certPem), "base64");
  const send = (res: import("node:http").ServerResponse, body: string | Buffer, headers: Record<string, string>) => {
    res.writeHead(200, headers);
    res.end(body);
  };
  createServer((req, res) => {
    const pathname = new URL(req.url ?? "/", "http://localhost").pathname;
    if (pathname === "/ca.mobileconfig") {
      return send(res, mobileconfig(certPem), {
        "Content-Type": "application/x-apple-aspen-config",
        "Content-Disposition": 'attachment; filename="flocon-ca.mobileconfig"',
      });
    }
    if (pathname === "/ca.pem") return send(res, certPem, { "Content-Type": "application/x-pem-file" });
    // Android 11+ refuses to install a CA opened from the browser ("must be installed in Settings"):
    // the file is downloaded as is, then installed from Settings › … › Install a certificate › CA certificate.
    if (pathname === "/ca.crt") {
      return send(res, der, {
        "Content-Type": "application/octet-stream",
        "Content-Disposition": 'attachment; filename="flocon-ca.crt"',
      });
    }
    return send(res, caPage(req), { "Content-Type": "text/html; charset=utf-8" });
  }).listen(CA_PORT, "0.0.0.0");
}

// ---------- Proxy servers ----------
type CA = { key: string; cert: string };

/**
 * Starts one mockttp proxy on `port`. `extraPort` is true for the ports of the `adb reverse` phones:
 * their calls are linked to a device by this port, the others by the IP of the phone.
 */
async function startProxyServer(ca: CA, port: number, extraPort: boolean): Promise<mockttp.Mockttp> {
  // Connections to this computer are not intercepted: they are the Flocon SDK talking to Flocon Desktop
  // (whose self-signed certificate the proxy would refuse), and they are not the traffic of the app anyway.
  const thisComputer = ["localhost", "127.0.0.1", ...localIPv4s()].map((hostname) => ({ hostname }));
  const proxy = mockttp.getLocal({ https: { ...ca, tlsPassthrough: thisComputer }, http2: "fallback" });
  await proxy.forAnyRequest().thenPassThrough();
  await proxy.forAnyWebSocket().thenPassThrough();

  // the device is picked when the request starts: its response goes to the same device
  const deviceByRequest = new Map<string, Device>();
  const startByRequest = new Map<string, number>();
  const deviceOfRequest = (ip: string | undefined) => deviceOf(clientIp(ip), extraPort ? port : null);
  const done = (id: string) => {
    const device = deviceByRequest.get(id);
    const start = startByRequest.get(id);
    deviceByRequest.delete(id);
    startByRequest.delete(id);
    return { device, start };
  };

  await proxy.on("request", async (req) => {
    const device = deviceOfRequest(req.remoteIpAddress);
    deviceByRequest.set(req.id, device);
    startByRequest.set(req.id, req.timingEvents.startTime);
    const body = await readBody(req.body);
    sendToFlocon(device, "network", "log_network_call_request", {
      floconCallId: req.id,
      floconNetworkType: "http",
      isMocked: false,
      url: req.url,
      method: req.method,
      startTime: req.timingEvents.startTime,
      requestHeaders: flatHeaders(req.headers),
      requestBody: body.text,
      requestSize: body.size,
    });
  });

  await proxy.on("response", async (res) => {
    const { device, start } = done(res.id);
    const headers = flatHeaders(res.headers);
    const contentType = headers["content-type"] ?? null;
    const isImage = !!contentType?.startsWith("image/");
    const body = isImage ? { text: null, size: await bodySize(res.body) } : await readBody(res.body);
    sendToFlocon(device ?? unlinkedDevice("unknown"), "network", "log_network_call_response", {
      floconCallId: res.id,
      floconNetworkType: "http",
      isMocked: false,
      durationMs: start ? Date.now() - start : 0,
      responseHttpCode: res.statusCode,
      responseContentType: contentType,
      responseBody: body.text,
      responseSize: body.size,
      responseHeaders: headers,
      isImage,
    });
  });

  await proxy.on("abort", (req) => {
    const { device, start } = done(req.id);
    sendToFlocon(device ?? deviceOfRequest(req.remoteIpAddress), "network", "log_network_call_response", {
      floconCallId: req.id,
      floconNetworkType: "http",
      isMocked: false,
      durationMs: start ? Date.now() - start : 0,
      responseHeaders: {},
      responseError: "Aborted",
      isImage: false,
    });
  });

  await proxy.on("tls-client-error", (e) => {
    // Usually: the CA is not installed / trusted on the phone, or the app pins its certificates.
    emit({ event: "tls-error", host: e.tlsMetadata?.sniHostname ?? null, reason: e.failureCause });
  });

  await proxy.start(port);
  return proxy;
}

// one proxy per `adb reverse` phone, started and stopped by the "devices" commands
const extraProxies = new Map<number, Promise<mockttp.Mockttp | null>>();

function syncExtraProxies(ca: CA, ports: number[]) {
  for (const [port, proxy] of extraProxies) {
    if (ports.includes(port)) continue;
    extraProxies.delete(port);
    proxy.then((p) => p?.stop()).catch(() => {});
  }
  for (const port of ports) {
    if (extraProxies.has(port)) continue;
    extraProxies.set(
      port,
      startProxyServer(ca, port, true).catch((e) => {
        console.error(`cannot listen on ${port}`, e);
        extraProxies.delete(port);
        return null;
      }),
    );
  }
}

function onCommand(ca: CA, line: string) {
  let command: any;
  try {
    command = JSON.parse(line);
  } catch {
    return;
  }
  if (command?.cmd !== "devices") return;
  deviceByIp = parseDevices(command.ips);
  const byPort = parseDevices(command.ports);
  deviceByPort = new Map([...byPort].map(([port, device]) => [Number(port), device]));
  reportedIps.clear(); // a phone still unknown is reported again
  syncExtraProxies(ca, [...deviceByPort.keys()].filter((port) => Number.isInteger(port) && port > 0));
}

// ---------- Main ----------
async function main() {
  const ca = await loadOrCreateCA();
  connectFlocon();
  startCAServer(ca.cert);

  const proxy = await startProxyServer(ca, PROXY_PORT, false);
  emit({ event: "ready", proxyPort: proxy.port, caPort: CA_PORT, ips: localIPv4s() });

  const stop = async () => {
    await Promise.all(
      [Promise.resolve(proxy), ...extraProxies.values()].map((p) => p.then((it) => it?.stop()).catch(() => {})),
    );
    process.exit(0);
  };
  process.on("SIGTERM", stop);
  process.on("SIGINT", stop);
  // Commands of Flocon Desktop. If Flocon dies without killing us, our stdin closes: stop too.
  createInterface({ input: process.stdin })
    .on("line", (line) => onCommand(ca, line))
    .on("close", stop);
}

// One bad request must not stop the capture of the others: log it and keep running.
process.on("unhandledRejection", (e) => console.error("unhandled rejection", e));
process.on("uncaughtException", (e) => console.error("uncaught exception", e));

main().catch((e) => {
  emit({ event: "error", message: String(e?.message ?? e) });
  process.exit(1);
});
