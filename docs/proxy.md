# Proxy mode (experimental)

!!! warning "Work in progress"
    The proxy binary and its process manager are in place; the toggle and the QR code in the desktop UI are not wired yet.

Flocon Desktop can run an HTTP(S) proxy on your computer. Any phone that uses it shows its traffic in the **Network** screen, **without the Flocon SDK** in the app. It also sees calls the SDK can't intercept: WebViews, third-party SDKs, other apps.

The in-app SDK stays the recommended mode: it knows which app made the call, links it with the other plugins, and needs no certificate.

## Set up the phone

1. Start the proxy in Flocon Desktop. It shows your computer IP and the proxy port (`8080` by default).
2. On the phone, on the same Wi-Fi network, set the Wi-Fi proxy to **Manual**, with that IP and port.
3. Open `http://<computer IP>:<CA port>/` on the phone and install the Flocon CA:
    - **iOS**: install the profile, then enable it in *Settings › General › About › Certificate Trust Settings*.
    - **Android**: install the certificate. Apps only trust user certificates if their `network_security_config` allows it, for example in debug builds:

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

Each phone appears as a device named `Proxy <phone IP>`.

## Limits

- Apps that pin their certificates refuse the proxy: their HTTPS calls fail.
- Mocks and bad network simulation are not applied to proxied calls yet.
- Bodies above 2 MB are truncated.

## How it works

The proxy is a small binary built from `proxy/` (TypeScript, [mockttp](https://github.com/httptoolkit/mockttp), compiled with Bun) and shipped in the app. Flocon Desktop starts it, and it connects back to the Flocon websocket like any SDK, sending regular `network` plugin messages (see `internal-docs/protocol/network.md`).
