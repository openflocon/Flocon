<img width="100" height="100" alt="flocon_small" src="https://github.com/user-attachments/assets/27143843-fce2-4c74-96d8-a0b35a8fccde" />     

**Flocon** is an advanced debugging and inspection tool built with **Kotlin Multiplatform (KMP)**, designed to work seamlessly across Android and desktop environments.  

Inspired from [Flipper](https://github.com/facebook/flipper) by Meta, while leveraging **modern Kotlin multiplatform architecture** for networking, databases, analytics, and UI data visualization.

It allows developers to connect a Kotlin Multiplatform or Android app to their computer and launch a **desktop interface** that can **observe, inspect, and interact with the running app** in real time — across shared Kotlin code and platform-specific implementations.

<img width="1294" height="837" alt="Screenshot 2025-09-12 at 15 39 45" src="https://github.com/user-attachments/assets/3d585adb-6441-4cdb-ad25-69d771ad4ff6" />

----> [Getting Started](setup.md) <----

**Works on**
<table>
    <thead>
        <tr>
            <th></th>
            <th>Android</th>
            <th>Desktop (jvm)</th>
            <th>iOS (simulator)</th>
            <th>iOS (device)</th>
            <th>wasm</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td class="feature-name">Network</td>
            <td>✅</td>
            <td>✅</td>
            <td>✅</td>
            <td>❌</td>
            <td>❌</td>
        </tr>
        <tr>
            <td class="feature-name">Database</td>
            <td>✅</td>
            <td>✅</td>
            <td>✅</td>
            <td>❌</td>
            <td>❌</td>
        </tr>
        <tr>
            <td class="feature-name">Preference</td>
            <td>✅</td>
            <td>❌</td>
            <td>❌</td>
            <td>❌</td>
            <td>❌</td>
        </tr>
        <tr>
            <td class="feature-name">Table</td>
            <td>✅</td>
            <td>✅</td>
            <td>✅</td>
            <td>❌</td>
            <td>❌</td>
        </tr>
        <tr>
            <td class="feature-name">Analytics</td>
            <td>✅</td>
            <td>✅</td>
            <td>✅</td>
            <td>❌</td>
            <td>❌</td>
        </tr>
        <tr>
            <td class="feature-name">Deeplink</td>
            <td>✅</td>
            <td>❌</td>
            <td>❌</td>
            <td>❌</td>
            <td>❌</td>
        </tr>
        <tr>
            <td class="feature-name">Files</td>
            <td>✅</td>
            <td>❌</td>
            <td>❌</td>
            <td>❌</td>
            <td>❌</td>
        </tr>
        <tr>
            <td class="feature-name">Images</td>
            <td>✅</td>
            <td>✅</td>
            <td>✅</td>
            <td>❌</td>
            <td>❌</td>
        </tr>
        <tr>
            <td class="feature-name">Dashboards</td>
            <td>✅</td>
            <td>❌</td>
            <td>❌</td>
            <td>❌</td>
            <td>❌</td>
        </tr>
    </tbody>
</table>

With Flocon, you gain deep access to critical app internals, such as
- network requests (HTTP, gRPC, GraphQL, WebSockets)
- mock network calls
- local storage (sharedpref, databases, app files)
- analytics events (and custom events)
- debug menu displayed on the desktop
- deeplinks

and more — without needing root access or tedious ADB commands.

It’s designed for modern multiplatform development, accelerating debugging, QA, and iteration cycles.