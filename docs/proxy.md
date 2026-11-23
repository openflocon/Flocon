# Proxy mode (experimental)

!!! note
    The proxy runs with the Node.js of your computer (18 or later), found even when it comes from Homebrew, nvm or Volta. Without Node.js, Flocon downloads a standalone proxy (~100 MB) the first time you activate it, then keeps it.

Flocon Desktop can run an HTTP(S) proxy on your computer. Any phone that uses it shows its traffic in the **Network** screen, **without the Flocon SDK** in the app. It also sees calls the SDK can't intercept: WebViews, third-party SDKs, other apps.

The in-app SDK stays the recommended mode: it knows which app made the call, links it with the other plugins, and needs no certificate.

## Set up the phone

1. In Flocon Desktop, click the router button of the top bar, then **Activate**. It stays on at the next launch. The dialog shows your computer IP, the proxy port (`8080` by default), a QR code to the certificate page, and the steps below for Android and iOS.
2. On the phone, on the same Wi-Fi network, set the Wi-Fi proxy to **Manual**, with that IP and port.
3. Scan the QR code (or open `http://<computer IP>:<CA port>/` on the phone) and install the Flocon CA:
    - **iOS**: install the profile, then enable it in *Settings › General › About › Certificate Trust Settings*.
    - **Android**: download the certificate, then install it from *Settings › Security › Encryption & credentials › Install a certificate › CA certificate* (search "CA certificate" in Settings if the path differs). Since Android 11, a CA certificate opened from the browser is refused ("must be installed in Settings"). Apps only trust user certificates if their `network_security_config` allows it, for example in debug builds:

```xml
<network-security-config>
    <debug-overrides>
        <trust-anchors>
            <certificates src="user" />
        </trust-anchors>
    </debug-overrides>
</network-security-config>
```

The iOS Simulator uses the network of the Mac: set the proxy in the macOS network settings instead.

Each phone appears as a device named `Proxy <phone IP>`, in its own **Proxy** group of the device selector, with a router icon: it is kept apart from the devices that use the SDK.

## Limits

- An app with the Flocon SDK keeps working with the proxy on: connections to your computer go through untouched.

- Apps that pin their certificates refuse the proxy: their HTTPS calls fail.
- Mocks and bad network simulation are not applied to proxied calls yet.
- Bodies above 2 MB are truncated.

## How it works

The proxy is a small binary built from `proxy/` (TypeScript, [mockttp](https://github.com/httptoolkit/mockttp), compiled with Bun) and shipped in the app. Flocon Desktop starts it, and it connects back to the Flocon websocket like any SDK, sending regular `network` plugin messages (see `internal-docs/protocol/network.md`).
