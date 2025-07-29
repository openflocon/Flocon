# 🐕 Flocon

**Flocon** is an advanced debugging and inspection tool for Android applications, inspired by [Flipper](https://github.com/facebook/flipper) by Meta.

It allows developers to connect an Android device to their computer and launch a desktop interface that can **observe, inspect, and interact with the running mobile app** in real time.

With Flocon, you gain deep access to critical app internals — such as network requests, local storage, analytics events, and more — without needing root access or tedious ADB commands. It’s designed to accelerate development, QA, and debugging workflows.

---

## 🚀 What Can Flocon Do?

Once your Android device is connected and your app includes the Flocon SDK, you can use the desktop companion app to access the following features:

---

### 📡 Network Request Inspector

Flocon captures **all outgoing network requests** made by the Android app — whether they’re simple REST API calls or complex multipart uploads — and displays them in an organized UI.

For each request, you can inspect:

- HTTP method (GET, POST, etc.)
- Full URL
- Request headers and body
- Response headers and body
- Status code and response time
- Timestamp

This feature is invaluable for diagnosing backend issues, debugging unexpected API failures, and verifying request payloads and authentication headers.

---

### 📊 Analytics Event Viewer

Flocon shows a real-time stream of **analytics events** emitted by your application. Whether you’re using Firebase Analytics, Segment, or a custom solution, the Flocon SDK can be plugged and forward these events to the desktop UI.

Each event includes:

- The event name
- Parameters and metadata
- Timestamps

This is especially useful for QA teams and product analysts to validate that the right events are triggered at the right time, with the correct payloads.

---

### 🗝 SharedPreferences Explorer & Editor

Flocon provides complete access to your app’s **SharedPreferences**, which often store user tokens, feature flags, configuration options, and more.

Key capabilities include:

- Browsing all preference files
- Viewing and filtering key-value pairs
- Inspecting primitive values and JSON structures
- **Editing values on the fly** from the desktop UI

This is an extremely powerful way to test different user scenarios or simulate app states, without needing to rebuild the app or manually trigger edge cases.

---

### 🧩 Database Query Tool

Flocon gives you direct access to your app’s **local databases** (SQLite, Room, etc.), with a clean interface for exploring and querying data.

Features include:

- Listing all available databases
- Running **custom SQL queries**

This makes it easy to debug persistent storage issues, verify migrations, or test app behavior with specific data sets — all without leaving your IDE.

---

### 📁 File Explorer

Flocon allows you to explore the **internal file storage** of your Android application — something that typically requires ADB and knowledge of Android's file system.

From the desktop app, you can:

- Browse directories within the app's sandbox
- View file metadata (size, modification date, path)
- `TO DEVELOP :` Open or download files for inspection
- `TO DEVELOP :`Preview text, JSON, or binary blobs

This feature is ideal for inspecting log files, cache data, downloaded assets, or exported config files.

---

### 📈 Configurable Dashboards (from the mobile app)

Your Android application can define and expose **custom dashboards**, which Flocon renders dynamically in the desktop interface.

Use cases include:

- Displaying live business metrics
- Monitoring app state variables
- Debugging real-time values (e.g., geolocation, battery, app mode)
- Real time in-app variables editions
- Perform from the desktop app mobile callbacks

Dashboards are defined programmatically on the mobile side via the SDK, and they update live as data changes — making them ideal for live demos, QA testing, or in-field diagnostics.

---

### 📋 Configurable Data Tables

In addition to dashboards, Flocon supports structured **data tables** that can be configured and updated by the mobile app.

These tables can be used to visualize:

- Lists of active users
- Items in memory or cache
- Custom logs or metrics
- Backend response simulations

Tables are interactive, scrollable, and sortable, and they give developers and testers a straightforward way to inspect lists or collections in real time.

---

### 🔗 Deeplink Launcher

Flocon includes a **deeplink runner**, which lists all the deeplinks supported by your app (either auto-discovered or manually registered).

From the desktop UI, you can:

- Browse available deeplinks
- Enter parameters interactively
- Execute deeplinks directly on the device
- Instantly navigate to specific app screens

No more typing long `adb shell am start` commands — Flocon makes deeplink testing accessible and efficient.

---

## 🧰 Requirements

- An Android device with USB debugging enabled
- Android Studio or SDK tools installed
- ADB (Android Debug Bridge) accessible from your system path
- Flocon Desktop app (JVM-based)
- Flocon SDK integrated into your Android app

---

## 🛠️ Getting Started

1. **Add flocon to your android app**

```
implementation("io.github.openflocon:flocon:$LAST_VERSION")
```

2. **Initialize flocon**
in your `Application.kt`
```
Flocon.initialize(this)
```

3. okhttp
4. grpc
