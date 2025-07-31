<img width="100" height="100" alt="flocon_small" src="https://github.com/user-attachments/assets/27143843-fce2-4c74-96d8-a0b35a8fccde" />     

**Flocon** is an advanced debugging and inspection tool for Android applications, inspired by [Flipper](https://github.com/facebook/flipper) by Meta.

It allows developers to connect an Android device to their computer and launch a desktop interface that can **observe, inspect, and interact with the running mobile app** in real time.

With Flocon, you gain deep access to critical app internals — such as
- network requests (http, images, grpc, graphql)
- local storage (sharedpref, databases, app files)
- analytics events (and custom events)
- debug menu displayed on the desktop
- **deeplinks**

and more — without needing root access or tedious ADB commands. It’s designed to accelerate development, QA, and debugging workflows.

<img width="600" height="389" alt="Capture d’écran 2025-07-31 à 23 29 44" src="https://github.com/user-attachments/assets/1feba7ff-5328-4c9f-ad71-e35e96e677cb" />

---

## 🚀 What Can Flocon Do?

Once your Android device is connected and your app includes the Flocon SDK, you can use the desktop companion app to access the following features:

---

🛠️ Getting Started

`This Android library is lightweight, contributing just 140KB to the overall app size`

in your module .kts

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon.svg)](https://search.maven.org/artifact/io.github.openflocon/flocon)
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

<img width="1199" height="771" alt="Capture d’écran 2025-07-31 à 23 29 44" src="https://github.com/user-attachments/assets/c082c9d8-d41c-415d-8762-706c5fc24a2b" />
<img width="1195" height="771" alt="Capture d’écran 2025-07-31 à 23 29 37" src="https://github.com/user-attachments/assets/59631b28-37f0-472e-8164-5ebe631fd60a" />


Flocon captures **all outgoing network requests** made by the Android app — whether they’re simple REST API calls or complex multipart uploads — and displays them in an organized UI.

For each request, you can inspect:

- HTTP method (GET, POST, etc.)
- Full URL
- Request headers and body
- Response headers and body
- Status code and response time
- Timestamp

This feature is invaluable for diagnosing backend issues, debugging unexpected API failures, and verifying request payloads and authentication headers.

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon-okhttp-interceptor.svg)](https://search.maven.org/artifact/io.github.openflocon/flocon-okhttp-interceptor)

```
debugImplementation("io.github.openflocon:flocon-okhttp-interceptor:LAST_VERSION")
```

```kotlin
val okHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(FloconOkhttpInterceptor())
            .build()
```

### 🛰️ GraphQL Request Inspector

<img width="1199" height="766" alt="Capture d’écran 2025-07-31 à 23 30 10" src="https://github.com/user-attachments/assets/53250047-3f3d-473e-af5f-8e243491c679" />
<img width="1203" height="772" alt="Capture d’écran 2025-07-31 à 23 30 06" src="https://github.com/user-attachments/assets/19da2d6d-d04e-4092-b430-7d3f547d0168" />


Flocon also supports **GraphQL** requests via a dedicated Apollo interceptor.

Just like with REST, all outgoing GraphQL requests made through [Apollo Client](https://www.apollographql.com/docs/android/) are captured and displayed in Flocon’s interface — allowing you to debug your queries and mutations in real time.

For each GraphQL call, you can inspect:

- Response data or error payload
- Headers, status code, and response time
- TO DEVELOP: The operation type (query / mutation)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon-graphql-interceptor.svg)](https://search.maven.org/artifact/io.github.openflocon/flocon-graphql-interceptor)

`works only with with apollo 4+`
```
debugImplementation("io.github.openflocon:flocon-graphql-interceptor:LAST_VERSION")
```

```kotlin
 ApolloClient.Builder()
            .serverUrl(...)
            ...
            .addHttpInterceptor(FloconApolloInterceptor()) 
            .build()
```

### 🖼️ Downloaded Image Viewer

<img width="1195" height="771" alt="Capture d’écran 2025-07-31 à 23 30 01" src="https://github.com/user-attachments/assets/d22940bb-f007-41a4-9878-ee1820f20816" />
<img width="1202" height="773" alt="Capture d’écran 2025-07-31 à 23 29 50" src="https://github.com/user-attachments/assets/15506d35-1039-4934-8034-1174c3740f7a" />

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

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon-grpc-interceptor.svg)](https://search.maven.org/artifact/io.github.openflocon/flocon-grpc-interceptor)
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

## ✨ Upcoming features 

Flocon is still evolving, next features : 

- GraphQl Network interceptor
- Preview & Dowload files 

## 🧰 Requirements

- An Android device with USB debugging enabled
- Android Studio or SDK tools installed
- ADB (Android Debug Bridge) accessible from your system path
- Flocon Desktop app (JVM-based)
- Flocon SDK integrated into your Android app
- At least `kotlin 2.0.0`

---

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

## 🤝 Contributors

Thanks to these amazing people for making Flocon better every day!

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/florent37">
        <img src="https://avatars.githubusercontent.com/u/5754972?v=4" width="100px;" alt="Florent Champigny"/><br />
        <sub><b>florent37</b></sub>
      </a>
    </td>
  </tr>
</table>

## 🐶 Why the name "Flocon" ✨ ?

I was looking for a short, cute animal-inspired name — something in the spirit of "Flipper".  
I turned my head and saw my golden retriever, Flocon, smiling to me... and that was it. 
That was all the inspiration I needed.  

No brainstorming, no hesitation — just the perfect name at the perfect time.

<img width="540" height="501" alt="Flocon - Golden Retriever" src="https://github.com/user-attachments/assets/6ea7acd9-abea-4062-b375-17cb8337ce11" />
