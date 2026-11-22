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
//   {"event":"error","message":"..."}

import * as mockttp from "mockttp";
import { existsSync, mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { join } from "node:path";
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
  // (see internal-docs/protocol/README.md, Transport). `tls` is a Bun extension of the constructor.
  ws = new WebSocket(FLOCON_WS, { tls: { rejectUnauthorized: false } } as any);
  ws.onopen = () => {
    greetedDevices.clear(); // a new session needs a new handshake for every device
    flushQueue();
  };
  ws.onclose = () => setTimeout(connectFlocon, 1000);
  ws.onerror = () => ws?.close();
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

/** Sends a Flocon envelope on behalf of the phone at `ip`. */
function sendToFlocon(ip: string, plugin: string, method: string, body: unknown) {
  const envelope = (p: string, m: string, b: unknown) =>
    JSON.stringify({
      deviceId: `proxy-${ip}`,
      deviceName: `Proxy ${ip}`,
      appName: "Proxy",
      appPackageName: "flocon.proxy",
      appInstance: APP_INSTANCE,
      platform: "proxy",
      versionName: VERSION,
      plugin: p,
      method: m,
      body: JSON.stringify(b),
    });

  if (!greetedDevices.has(ip) && ws?.readyState === WebSocket.OPEN) {
    greetedDevices.add(ip);
    sendRaw(envelope("bonjour", "bonjour", { plugins: ["network"], connectionType: "wifi", sdk: "proxy" }));
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
  const uuid = () => crypto.randomUUID().toUpperCase();
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
const PAGE_TEXT = {
  en: {
    title: "Flocon Proxy",
    android: "Download the certificate",
    ios: "Install the profile",
    other: "Other phone: ",
    next: "Then follow the steps shown in Flocon Desktop.",
  },
  fr: {
    title: "Flocon Proxy",
    android: "Télécharger le certificat",
    ios: "Installer le profil",
    other: "Autre téléphone : ",
    next: "Puis suis les étapes affichées dans Flocon Desktop.",
  },
};

function caPage(req: Request): string {
  const ua = req.headers.get("user-agent") ?? "";
  const text = /^fr\b/i.test(req.headers.get("accept-language") ?? "") ? PAGE_TEXT.fr : PAGE_TEXT.en;
  const android = { href: "/ca.crt", label: text.android };
  const ios = { href: "/ca.mobileconfig", label: text.ios };
  // the button of the phone first, the other one in small below
  const [main, other] = /iPhone|iPad|iPod/i.test(ua) ? [ios, android] : [android, ios];
  return `<!doctype html><html><head><meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1"><title>${text.title}</title>
<style>
  :root { color-scheme: dark; }
  body { margin: 0; min-height: 100vh; display: flex; flex-direction: column; align-items: center; justify-content: center;
         gap: 20px; padding: 24px; box-sizing: border-box; background: #111111; color: #ECECEC;
         font: 16px -apple-system, system-ui, sans-serif; text-align: center; }
  h1 { margin: 0; font-size: 24px; }
  .main { display: block; width: 100%; max-width: 360px; padding: 18px; border-radius: 14px; background: #F5B440;
          color: #1C1405; font-weight: 700; font-size: 18px; text-decoration: none; }
  .other { color: #A3A3A3; font-size: 14px; }
  p { margin: 0; color: #A3A3A3; }
</style></head><body>
  <h1>${text.title}</h1>
  <a class="main" href="${main.href}">${main.label}</a>
  <p>${text.next}</p>
  <a class="other" href="${other.href}">${text.other}${other.label}</a>
</body></html>`;
}

function startCAServer(certPem: string) {
  const der = Buffer.from(pemToBase64Der(certPem), "base64");
  Bun.serve({
    port: CA_PORT,
    hostname: "0.0.0.0",
    fetch(req) {
      const { pathname } = new URL(req.url);
      if (pathname === "/ca.mobileconfig")
        return new Response(mobileconfig(certPem), {
          headers: {
            "Content-Type": "application/x-apple-aspen-config",
            "Content-Disposition": 'attachment; filename="flocon-ca.mobileconfig"',
          },
        });
      if (pathname === "/ca.pem") return new Response(certPem, { headers: { "Content-Type": "application/x-pem-file" } });
      // Android 11+ refuses to install a CA opened from the browser ("must be installed in Settings"):
      // the file is downloaded as is, then installed from Settings › … › Install a certificate › CA certificate.
      if (pathname === "/ca.crt")
        return new Response(der, {
          headers: {
            "Content-Type": "application/octet-stream",
            "Content-Disposition": 'attachment; filename="flocon-ca.crt"',
          },
        });
      return new Response(caPage(req), { headers: { "Content-Type": "text/html; charset=utf-8" } });
    },
  });
}

// ---------- Main ----------
async function main() {
  const ca = await loadOrCreateCA();
  connectFlocon();
  startCAServer(ca.cert);

  // Connections to this computer are not intercepted: they are the Flocon SDK talking to Flocon Desktop
  // (whose self-signed certificate the proxy would refuse), and they are not the traffic of the app anyway.
  const thisComputer = ["localhost", "127.0.0.1", ...localIPv4s()].map((hostname) => ({ hostname }));
  const proxy = mockttp.getLocal({ https: { ...ca, tlsPassthrough: thisComputer }, http2: "fallback" });
  await proxy.forAnyRequest().thenPassThrough();
  await proxy.forAnyWebSocket().thenPassThrough();

  const ipByRequest = new Map<string, string>();
  const startByRequest = new Map<string, number>();

  await proxy.on("request", async (req) => {
    const ip = clientIp(req.remoteIpAddress);
    ipByRequest.set(req.id, ip);
    startByRequest.set(req.id, req.timingEvents.startTime);
    const body = await readBody(req.body);
    sendToFlocon(ip, "network", "log_network_call_request", {
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
    const ip = ipByRequest.get(res.id) ?? "unknown";
    const start = startByRequest.get(res.id);
    ipByRequest.delete(res.id);
    startByRequest.delete(res.id);
    const headers = flatHeaders(res.headers);
    const contentType = headers["content-type"] ?? null;
    const isImage = !!contentType?.startsWith("image/");
    const body = isImage ? { text: null, size: await bodySize(res.body) } : await readBody(res.body);
    sendToFlocon(ip, "network", "log_network_call_response", {
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
    const ip = ipByRequest.get(req.id) ?? clientIp(req.remoteIpAddress);
    const start = startByRequest.get(req.id);
    ipByRequest.delete(req.id);
    startByRequest.delete(req.id);
    sendToFlocon(ip, "network", "log_network_call_response", {
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

  await proxy.start(PROXY_PORT);
  emit({ event: "ready", proxyPort: proxy.port, caPort: CA_PORT, ips: localIPv4s() });

  const stop = async () => {
    await proxy.stop().catch(() => {});
    process.exit(0);
  };
  process.on("SIGTERM", stop);
  process.on("SIGINT", stop);
  // If Flocon dies without killing us, our stdin closes: stop too.
  process.stdin.on("end", stop);
  process.stdin.resume();
}

// One bad request must not stop the capture of the others: log it and keep running.
process.on("unhandledRejection", (e) => console.error("unhandled rejection", e));
process.on("uncaughtException", (e) => console.error("uncaught exception", e));

main().catch((e) => {
  emit({ event: "error", message: String(e?.message ?? e) });
  process.exit(1);
});
