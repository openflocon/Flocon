---
title: Troubleshooting
description: Common issues and solutions when connecting Flocon Desktop to your mobile app.
---

# 🚨 Troubleshooting & FAQ

## Flocon Desktop Cannot See Device Calls

To allow Flocon to intercept and inspect network traffic, database queries, and custom events from your Android app, the app must be allowed to communicate with `localhost` (`127.0.0.1`), where the desktop companion communicates.

### 1. Allow Cleartext Traffic to Localhost

If your app uses a custom `networkSecurityConfig`, ensure you explicitly permit cleartext traffic to `localhost` and `127.0.0.1`.

=== "`AndroidManifest.xml`"

    ```xml
    <application
        android:name=".MyApp"
        android:networkSecurityConfig="@xml/network_security_config"
        ... >
    </application>
    ```

=== "`res/xml/network_security_config.xml`"

    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <network-security-config>
        <domain-config cleartextTrafficPermitted="true">
            <domain includeSubdomains="true">localhost</domain>
            <domain includeSubdomains="true">127.0.0.1</domain>
        </domain-config>
    </network-security-config>
    ```

---

## 2. ADB Connection Issues

!!! info "Verifying ADB Connectivity"
    Make sure your device is detected by ADB:
    ```bash
    adb devices
    ```
    If your device is in `unauthorized` state, check your phone screen for the USB debugging authorization prompt.

---

## 3. Version Mismatch

!!! warning "Keep Versions Aligned"
    Make sure the version of the Flocon SDK in your project matches the major/minor version of the **Flocon Desktop** client you have downloaded.
