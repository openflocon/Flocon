<img width="100" height="100" alt="flocon_small" src="https://github.com/user-attachments/assets/27143843-fce2-4c74-96d8-a0b35a8fccde" />     

**Flocon** is an advanced debugging and inspection tool built with **Kotlin Multiplatform (KMP)**, designed to work seamlessly across Android and desktop environments.  

Inspired from [Flipper](https://github.com/facebook/flipper) by Meta, while leveraging **modern Kotlin multiplatform architecture** for networking, databases, analytics, and UI data visualization.

It allows developers to connect a Kotlin Multiplatform or Android app to their computer and launch a **desktop interface** that can **observe, inspect, and interact with the running app** in real time — across shared Kotlin code and platform-specific implementations.

---

## 📖 Documentation

Visit the full documentation site for setup guides, feature details, and advanced usage:  
👉 **[openflocon.github.io/Flocon/](https://openflocon.github.io/Flocon/)**

### Table of Contents

- 🚀 **[Getting Started](https://openflocon.github.io/Flocon/setup)**
    - [Installation Guide](https://openflocon.github.io/Flocon/setup#installation)
    - [Basic Usage](https://openflocon.github.io/Flocon/setup#usage)
    - [macOS Specifics](https://openflocon.github.io/Flocon/macos-install)
- 📡 **[Networking](https://openflocon.github.io/Flocon/network)**
    - [HTTP Inspector](https://openflocon.github.io/Flocon/network#📡-network-request-inspector)
    - [Request Mocking](https://openflocon.github.io/Flocon/network#🎭-http-request-mocking)
    - [GraphQL](https://openflocon.github.io/Flocon/graphql)
    - [gRPC](https://openflocon.github.io/Flocon/grpc)
    - [WebSockets](https://openflocon.github.io/Flocon/websocket)
- 💾 **[Local Storage](https://openflocon.github.io/Flocon/database)**
    - [Database Explorer (Room & SQLite)](https://openflocon.github.io/Flocon/database)
    - [Shared Preferences (Android)](https://openflocon.github.io/Flocon/sharedpref)
- 🛠️ **[Advanced Debugging](https://openflocon.github.io/Flocon/dashboard)**
    - [Custom Dashboards](https://openflocon.github.io/Flocon/dashboard)
    - [Dynamic Data Tables](https://openflocon.github.io/Flocon/table)
    - [Deeplink Launcher](https://openflocon.github.io/Flocon/deeplink)
    - [File Explorer](https://openflocon.github.io/Flocon/files)
    - [Analytics Viewer](https://openflocon.github.io/Flocon/analytics)
    - [Image Previewer](https://openflocon.github.io/Flocon/image)
- ❓ **[Troubleshooting](https://openflocon.github.io/Flocon/troubleshooting)**

---

## ✨ Features at a Glance

| Feature | Android | KMP (JVM/iOS) |
|---|:---:|:---:|
| **Network (HTTP/Mocking)** | ✅ | ✅ |
| **Database (Room/SQLite)** | ✅ | ✅ |
| **SharedPreferences** | ✅ | ❌ |
| **Dashboards & Tables** | ✅ | ✅ |
| **Analytics & Images** | ✅ | ✅ |
| **Deeplink & Files** | ✅ | ❌ |

---

## 🧰 Requirements

- **Android**: Device/Emulator with USB debugging + ADB.
- **KMP**: Kotlin 2.0.0+ in your app.
- **Desktop**: Flocon Desktop app (JVM-based).

For detailed platform-specific requirements, see the [Setup Guide](https://openflocon.github.io/Flocon/setup).

---

## 🤝 Contributors

Thanks to these amazing people for making Flocon better every day!

[See all contributors](https://openflocon.github.io/Flocon/contributors)

---

## 🐶 Why the name "Flocon" ✨ ?

I was looking for a short, cute animal-inspired name — something in the spirit of "Flipper".  
I turned my head and saw my golden retriever, Flocon, smiling to me... and that was it. 

No brainstorming, no hesitation — just the perfect name at the perfect time.

<img width="540" height="501" alt="Flocon - Golden Retriever" src="https://github.com/user-attachments/assets/6ea7acd9-abea-4062-b375-17cb8337ce11" />

---

## License

Flocon is MIT licensed, as found in the [LICENSE](/LICENSE) file.
