## 🚨 Why Flocon Can’t See Your Device Calls (And How to Fix It) 🚨

To enable Flocon to intercept and inspect network traffic from your Android app, 
the app must be allowed to connect to `localhost` (typically `127.0.0.1`), which is where the desktop companion listens for traffic.

**If you're already using a custom `networkSecurityConfig`, make sure it includes a rule to allow cleartext traffic to `localhost`**

AndroidManifest.xml
```xml
<application
        android:networkSecurityConfig="@xml/network_security_config"/>
```

network_security_config.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">localhost</domain>
        <domain includeSubdomains="true">127.0.0.1</domain>
    </domain-config>
</network-security-config>
```
