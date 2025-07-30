# 🐕 Flocon

**Flocon** is an advanced debugging and inspection tool for Android applications, inspired by [Flipper](https://github.com/facebook/flipper) by Meta.

It allows developers to connect an Android device to their computer and launch a desktop interface that can **observe, inspect, and interact with the running mobile app** in real time.

With Flocon, you gain deep access to critical app internals — such as network requests, local storage, analytics events, and more — without needing root access or tedious ADB commands. It’s designed to accelerate development, QA, and debugging workflows.

---

## 🚀 What Can Flocon Do?

Once your Android device is connected and your app includes the Flocon SDK, you can use the desktop companion app to access the following features:

---

🛠️ Getting Started

in your module .kts
https://img.shields.io/maven-central/v/io.github.openflocon/flocon.svg
```
// use only on a debug buildType, do not distribute on the playstore build !
debugImplementation("io.github.openflocon:flocon:LAST_VERSION")
```

in your `Application.kt`
```kotlin
Flocon.initialize(this)
```

Download & install the last `Desktop client`

https://github.com/openflocon/Flocon/releases

### 📡 Network Request Inspector

<img width="1200" height="780" alt="Network1" src="https://github.com/user-attachments/assets/cc7aeead-33e8-4ca4-8572-58607edc26c6" />
<img width="1199" height="774" alt="Network2" src="https://github.com/user-attachments/assets/63725cda-4e21-4f0f-b5a9-53d2e3c46b26" />


Flocon captures **all outgoing network requests** made by the Android app — whether they’re simple REST API calls or complex multipart uploads — and displays them in an organized UI.

For each request, you can inspect:

- HTTP method (GET, POST, etc.)
- Full URL
- Request headers and body
- Response headers and body
- Status code and response time
- Timestamp

This feature is invaluable for diagnosing backend issues, debugging unexpected API failures, and verifying request payloads and authentication headers.

https://img.shields.io/maven-central/v/io.github.openflocon/flocon-okhttp-interceptor.svg
```
debugImplementation("io.github.openflocon:flocon-okhttp-interceptor:LAST_VERSION")
```

```kotlin
val okHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(FloconOkhttpInterceptor())
            .build()
```

### 🖼️ Downloaded Image Viewer

<img width="1204" height="769" alt="Capture d’écran 2025-07-30 à 08 09 56" src="https://github.com/user-attachments/assets/f85fdd00-4ddf-4978-9e83-ab6007ddc2c0" />

Flocon captures and displays **images downloaded by the Android app**, giving you a clear, visual representation of media fetched over the network — such as avatars, product thumbnails, banners, or any other images requested at runtime.

For each image, Flocon shows:

- A live **thumbnail preview** of the image  
- The **URL** from which it was downloaded
- The **download timestamp**  

This feature is extremely useful for:

- Verifying that images are loading correctly and not broken  
- Debugging CDN issues, placeholders, or misconfigured URLs  
- Comparing image quality and compression at runtime  
- Inspecting lazy loading or image caching behaviors  

Whether you're working on UI/UX, performance optimization, or just debugging a missing image, this tool gives you **immediate visibility** into every image fetched by your app.

Usage with coil

```kotlin
// just add your okhttp client (with the flipper interceptor)
SingletonImageLoader.setSafe {
        ImageLoader.Builder(context = context)
            .components {
                add(
                    coil3.network.okhttp.OkHttpNetworkFetcherFactory(
                        callFactory = {
                            okHttpClient
                        },
                    ),
                )
            }
            .build()
}
```

---

### 📊 Analytics Event Viewer
<img width="1196" height="774" alt="Analytics" src="https://github.com/user-attachments/assets/6f485b4e-874b-4fdc-afab-752c4cd1ea3a" />

Flocon shows a real-time stream of **analytics events** emitted by your application. Whether you’re using Firebase Analytics, Segment, or a custom solution, the Flocon SDK can be plugged and forward these events to the desktop UI.

Each event includes:

- The event name
- Parameters and metadata
- Timestamps

This is especially useful for QA teams and product analysts to validate that the right events are triggered at the right time, with the correct payloads.

```kotlin
Flocon.analytics("firebase").logEvents(
     AnalyticsEvent(
         eventName = "clicked user",
         "userId" analyticsProperty "1024",
         "username" analyticsProperty "florent",
         "index" analyticsProperty "3",
    ),
    AnalyticsEvent(
         eventName = "opened profile",
         "userId" analyticsProperty "2048",
         "username" analyticsProperty "kevin",
         "age" analyticsProperty "34",
    ),
```

---

### 🗝 SharedPreferences Explorer & Editor
<img width="1197" height="768" alt="SharedPreferences" src="https://github.com/user-attachments/assets/c9fe264b-10d4-4f30-89e8-2d622b54899b" />

Flocon provides complete access to your app’s **SharedPreferences**, which often store user tokens, feature flags, configuration options, and more.

Key capabilities include:

- Browsing all preference files
- Viewing and filtering key-value pairs
- Inspecting primitive values and JSON structures
- **Editing values on the fly** from the desktop UI

This is an extremely powerful way to test different user scenarios or simulate app states, without needing to rebuild the app or manually trigger edge cases.

---

### 🧩 Database Query Tool

<img width="1203" height="774" alt="Database" src="https://github.com/user-attachments/assets/abf53cc3-0cec-42a4-941b-b9ec03ea9635" />

Flocon gives you direct access to your app’s **local databases** (SQLite, Room, etc.), with a clean interface for exploring and querying data.

Features include:

- Listing all available databases
- Running **custom SQL queries**

This makes it easy to debug persistent storage issues, verify migrations, or test app behavior with specific data sets — all without leaving your IDE.

---

### 📁 File Explorer

<img width="1196" height="770" alt="Files" src="https://github.com/user-attachments/assets/d0317559-85f6-47ed-9cf3-11f07b020a09" />

Flocon allows you to explore the **internal file storage** of your Android application — something that typically requires ADB and knowledge of Android's file system.

From the desktop app, you can:

- Browse directories within the app's sandbox
- View file metadata (size, modification date, path)
- `TO DEVELOP :` Open or download files for inspection
- `TO DEVELOP :`Preview text, JSON, or binary blobs

This feature is ideal for inspecting log files, cache data, downloaded assets, or exported config files.

---

### 📈 Configurable Dashboards (from the mobile app)

<img width="1203" height="770" alt="Dashboards" src="https://github.com/user-attachments/assets/d918c15c-72b2-412c-b97f-a409dcdf1737" />

Your Android application can define and expose **custom dashboards**, which Flocon renders dynamically in the desktop interface.

Use cases include:

- Displaying live business metrics
- Monitoring app state variables
- Debugging real-time values (e.g., geolocation, battery, app mode)
- Real time in-app variables editions
- Perform from the desktop app mobile callbacks

Dashboards are defined programmatically on the mobile side via the SDK, and they update live as data changes — making them ideal for live demos, QA testing, or in-field diagnostics.

```kotlin
userFlow.collect { user ->
     Flocon.dashboard(id = "main") {
        user?.let {
            section(name = "User") {
                text(label = "username", value = user.userName)
                text(label = "fullName", value = user.fullName, color = Color.Red.toArgb())
                text(label = "user id", value = user.id)
                button(
                    text = "Change User Id",
                    id = "changeUserId",
                    onClick = {
                        userFlow.update { it.copy(userName = "__flo__") }
                    }
                )
                textField(
                    label = "Update Name",
                    placeHolder = "name",
                    id = "changeUserName",
                    value = user.fullName,
                    onSubmitted = { value ->
                        userFlow.update { it.copy(fullName = value) }
                    })
            }
        }
    }
}
```

---

### 📋 Configurable Data Tables

<img width="1196" height="768" alt="tables" src="https://github.com/user-attachments/assets/ff3090fa-8f37-4138-a492-20b9159314af" />

In addition to dashboards, Flocon supports structured **data tables** that can be configured and updated by the mobile app.

These tables can be used to visualize:

- Lists of active users
- Items in memory or cache
- Custom logs or metrics
- Backend response simulations

Tables are interactive, scrollable, and they give developers and testers a straightforward way to inspect lists or collections in real time.

To create a dynamic row :
```kotlin
Flocon.table("analytics").log(
   "name" toParam "nameValue",
   "value1" toParam "value1Value",
   "value2" toParam "value2Value",
)
```

---

### 🔗 Deeplink Launcher
<img width="1201" height="772" alt="Deeplink" src="https://github.com/user-attachments/assets/bb09dd62-439f-466b-baac-f9253b0c9246" />

Flocon includes a **deeplink runner**, which lists all the deeplinks supported by your app (either auto-discovered or manually registered).

From the desktop UI, you can:

- Browse available deeplinks
- Enter parameters interactively
- Execute deeplinks directly on the device
- Instantly navigate to specific app screens

No more typing long `adb shell am start` commands — Flocon makes deeplink testing accessible and efficient.

**You can configure deeplinks directly from your android code !**
```kotlin
Flocon.deeplinks(
        listOf(
            Deeplink("flocon://home"),
            Deeplink("flocon://test"),
            Deeplink(
                "flocon://user/[userId]",
                label = "User"
            ),
            Deeplink(
                "flocon://post/[postId]?comment=[commentText]",
                label = "Post",
                description = "Open a post and send a comment"
            ),
        )
    )
```

---

# Grpc

<img width="1207" height="774" alt="GRPC" src="https://github.com/user-attachments/assets/992541b3-3f61-4151-b6f5-0ed1b06ee7e4" />

Similar to network inteceptions, Flocon works with grpc 

https://img.shields.io/maven-central/v/io.github.openflocon/flocon-grpc-interceptor.svg
```
debugImplementation("io.github.openflocon:flocon-grpc-interceptor:LAST_VERSION")
```

```kotlin
ManagedChannelBuilder
            ...
            .intercept(
                FloconGrpcInterceptor()
            )
            .build()
```

## 🧰 Requirements

- An Android device with USB debugging enabled
- Android Studio or SDK tools installed
- ADB (Android Debug Bridge) accessible from your system path
- Flocon Desktop app (JVM-based)
- Flocon SDK integrated into your Android app
- At least `kotlin 2.0.0`

---
