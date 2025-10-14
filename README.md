<img width="100" height="100" alt="flocon_small" src="https://github.com/user-attachments/assets/27143843-fce2-4c74-96d8-a0b35a8fccde" />     

**Flocon** is an advanced debugging and inspection tool for Android applications, inspired by [Flipper](https://github.com/facebook/flipper) by Meta.

It allows developers to connect an Android device to their computer and launch a desktop interface that can **observe, inspect, and interact with the running mobile app** in real time.

`Flocon Desktop is a Kotlin Multiplatform project structured similarly to an Android app, using ViewModels, Room, Ktor, and Coroutines. The project is open to contributions — feel free to submit a pull request!`

With Flocon, you gain deep access to critical app internals — such as
- network requests (http, images, grpc, graphql, websockets)
- mock network calls
- local storage (sharedpref, databases, app files)
- analytics events (and custom events)
- debug menu displayed on the desktop
- **deeplinks**

and more — without needing root access or tedious ADB commands. It’s designed to accelerate development, QA, and debugging workflows.

<img width="1294" height="837" alt="Screenshot 2025-09-12 at 15 39 45" src="https://github.com/user-attachments/assets/3d585adb-6441-4cdb-ad25-69d771ad4ff6" />

---

## 🚀 What Can Flocon Do?

Once your Android device is connected and your app includes the Flocon SDK, you can use the desktop companion app to access the following features:

---

🛠️ Getting Started

`This Android library is lightweight, contributing just 140KB to the overall app size`

in your module .kts

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon.svg)](https://search.maven.org/artifact/io.github.openflocon/flocon)
```
debugImplementation("io.github.openflocon:flocon:LAST_VERSION")
releaseImplementation("io.github.openflocon:flocon-no-op:LAST_VERSION")
```

in your `Application.kt`
```kotlin
Flocon.initialize(this)
```

or in your .toml

```toml
[versions]
flocon = "LASTEST_VERSION"

[libraries]
flocon = { module = "io.github.openflocon:flocon", version.ref = "flocon" }
```

Download & install the last `Desktop client`

https://github.com/openflocon/Flocon/releases

### 📡 Network Request Inspector

<img width="1291" height="834" alt="Screenshot 2025-09-12 at 15 39 55" src="https://github.com/user-attachments/assets/48f86fdf-f552-4f68-abe2-8d61229ccb27" />

<img width="1292" height="833" alt="Screenshot 2025-09-12 at 15 40 03" src="https://github.com/user-attachments/assets/c0f74bb4-85f3-4ced-b156-78dfae0189f3" />

Flocon captures **all outgoing network requests** made by the Android app — whether they’re simple REST API calls or complex multipart uploads — and displays them in an organized UI.

For each request, you can inspect:

- HTTP method (GET, POST, etc.)
- Full URL
- Request headers and body
- Response headers and body
- Status code and response time
- Timestamp

This feature is invaluable for diagnosing backend issues, debugging unexpected API failures, and verifying request payloads and authentication headers.

#### 🎭 HTTP Request Mocking

<img width="1293" height="836" alt="Screenshot 2025-09-12 at 15 40 38" src="https://github.com/user-attachments/assets/3a529e3f-488e-4dba-aee1-fc6f70efcb08" />

Beyond simple inspection, Flocon now allows you to mock HTTP requests. This powerful feature gives you full control over your app's network layer without needing to change any code. You can intercept specific network calls and provide custom responses, making it easy to test various scenarios.

With this feature, you can:

- Simulate network errors: Test how your app handles different HTTP status codes (e.g., 404 Not Found, 500 Server Error).
- Create test data: Mock responses with specific data to test different UI states, even if your backend isn't ready yet.
- Create a new mock from an existing request, then test your app with some differences inside the prefious body
- Reduce dependencies: Develop and test features without needing a stable internet connection or a complete backend environment.

#### With OkHttp

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon-okhttp-interceptor.svg)](https://search.maven.org/artifact/io.github.openflocon/flocon-okhttp-interceptor)

```
debugImplementation("io.github.openflocon:flocon-okhttp-interceptor:LAST_VERSION")
releaseImplementation("io.github.openflocon:flocon-okhttp-interceptor-no-op:LAST_VERSION")
```

```kotlin
val okHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(FloconOkhttpInterceptor())
            .build()
```

#### With Ktor 

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon-ktor-interceptor.svg)](https://search.maven.org/artifact/io.github.openflocon/flocon-ktor-interceptor)

tested with ktor `3.2.3`

```
debugImplementation("io.github.openflocon:flocon-ktor-interceptor:LAST_VERSION")
```

```kotlin
val httpClient = HttpClient(OkHttp) { // works with all clients, not only OkHttp
    install(FloconKtorPlugin)
    ...
}
```

### 💬 Inspect Websockets

<img width="1442" height="572" alt="Screenshot 2025-10-04 at 23 44 57" src="https://github.com/user-attachments/assets/49cef28f-87c9-4af7-a929-63d428d99f9e" />

Flocon doesn’t stop at HTTP — it also captures **all WebSocket communications** made by your Android app.  
This allows you to inspect real-time data exchanges between your app and the server with full visibility.

For each WebSocket connection, you can inspect:

- Connection URL  
- **Sent and received frames** (text, binary, ping/pong)  
- **Timestamps** and message order  
- **Payloads**  
- **Closes**

With this feature, you can:

- Debug real-time features like chat, live feeds, or multiplayer updates  
- Verify the exact content of messages exchanged  
- Diagnose disconnection or synchronization issues  

#### With OkHttp3

Flocon-Okhttp-Interceptor has built-in websocket methods (⚠️ it's not possible through interceptors ⚠️) 

To log outgoing messages 
```kotlin
webSocket.sendWithFlocon("\"$text\"") // extension method that log to Flocon and performs the send
```

To log incoming messages 
```kotlin
val request = Request.Builder()
       .url("wss://.......")
       .build()
val listener = object : WebSocketListener() {
      // your listener
}

webSocket = client.newWebSocket(
      request,
      listener.listenWithFlocon(id = "wss://......."), // extension method that wraps an existing WebSocketListener
    )
}
```

#### 🧰 Manually 

If you are using other websockets libs than okhttp, you can easily forward events to FloconWebSocket

To log outgoing messages 
```kotlin
val message = "hello"

webSocket.send(message)

floconLogWebSocketEvent(
    FloconWebSocketEvent(
        websocketUrl = "ws://..."
        event = FloconWebSocketEvent.Event.SendMessage,
        message = message,
    )
)
```

To log incoming messages 
```kotlin
myCustomWebSocket.onReceived {
    floconLogWebSocketEvent(
        FloconWebSocketEvent(
        websocketUrl = "ws://..."
        event = FloconWebSocketEvent.Event.ReceiveMessage,
        message = it,
    )
    // handle your message
)
```

### 🛰️ GraphQL Request Inspector

Flocon also supports **GraphQL** requests via a dedicated Apollo interceptor.

Just like with REST, all outgoing GraphQL requests made through [Apollo Client](https://www.apollographql.com/docs/android/) are captured and displayed in Flocon’s interface — allowing you to debug your queries and mutations in real time.


For each GraphQL call, you can inspect:

- Response data or error payload
- Headers, status code, and response time
- The operation type (query / mutation)

```kotlin
ApolloClient.Builder()
            // just set your already configured with flocon okhttp interceptor client
            .okHttpClient(client)
            // regular builder methods
            .build()
```

### 🖼️ Downloaded Image Viewer

<img width="1297" height="838" alt="Screenshot 2025-09-12 at 15 40 53" src="https://github.com/user-attachments/assets/5f83ce95-0b03-4bfd-9d67-099c7b5ca5cc" />

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

<img width="1296" height="837" alt="Screenshot 2025-09-12 at 15 41 27" src="https://github.com/user-attachments/assets/e3f2a6ab-bf25-48ac-b9fe-8ea3f81206a1" />

<img width="1294" height="838" alt="Screenshot 2025-09-12 at 15 41 32" src="https://github.com/user-attachments/assets/b7be4f8d-afcb-4bbc-8da4-c09e1cd240a6" />

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

<img width="1295" height="836" alt="Screenshot 2025-09-12 at 15 41 04" src="https://github.com/user-attachments/assets/03c3278b-dc2f-4943-ba17-b18030e204ea" />

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

<img width="1295" height="838" alt="Screenshot 2025-09-12 at 15 41 20" src="https://github.com/user-attachments/assets/1c22c813-20c4-4e34-bcf8-6cc511deab21" />

Flocon allows you to explore the **internal file storage** of your Android application — something that typically requires ADB and knowledge of Android's file system.

From the desktop app, you can:

- Browse directories within the app's sandbox
- View file metadata (size, modification date, path)
- `TO DEVELOP :` Open or download files for inspection
- `TO DEVELOP :`Preview text, JSON, or binary blobs

This feature is ideal for inspecting log files, cache data, downloaded assets, or exported config files.

---

### 📈 Configurable Dashboards (from the mobile app)

<img width="1027" height="561" alt="Screenshot 2025-09-12 at 15 45 05" src="https://github.com/user-attachments/assets/056feafc-fda9-46ff-aaf0-4b8a0801b72e" />
<img width="373" height="312" alt="Screenshot 2025-09-12 at 15 44 57" src="https://github.com/user-attachments/assets/03b7ed4a-4de0-472d-87aa-850b33a3843f" />

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

<img width="1293" height="836" alt="Screenshot 2025-09-12 at 15 41 39" src="https://github.com/user-attachments/assets/eeaa30fb-6567-437a-96a4-dff44c6c6a54" />

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

Similar to network inteceptions, Flocon works with grpc 

it works with `io.grpc:grpc-android` : https://github.com/grpc/grpc-java

> [!WARNING]
> please ensure your version is at lease `1.70.0`

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon-grpc-interceptor.svg)](https://search.maven.org/artifact/io.github.openflocon/flocon-grpc-interceptor)

> [!IMPORTANT]
> While dealing with protobuf on Android projects, it's best to use its lighter artifact (protobuf-javalite or protobuf-kotlin-lite). 
> It might be that your project needs the larger protobuf version (protobuf-java or protobuf-kotlin).
> Flocon offers two interceptor artifacts that leverage a different JSON formatter. It declutters the JSON printing by removing unwanted fields with a dedicated formatter depending on the protobuf library.
> Make sure you choose the correct artifact.

```
 // If you're using protobuf-javalite or protobuf-kotlin-lite
implementation("com.google.protobuf:protobuf-kotlin-lite:$PROTOBUF_VERSION")

implementation("io.github.openflocon:grpc-interceptor-lite:LAST_VERSION")
```
or
```
// If you're using protobuf-java or protobuf-kotlin
implementation("com.google.protobuf:protobuf-java:$PROTOBUF_VERSION")

implementation("io.github.openflocon:grpc-interceptor:LAST_VERSION") 
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

- Preview & Dowload files 

## 🧰 Requirements

- An Android device with USB debugging enabled
- Android Studio or SDK tools installed
- ADB (Android Debug Bridge) accessible from your system path
- Flocon Desktop app (JVM-based)
- Flocon SDK integrated into your Android app
- At least `kotlin 2.0.0` in your Android app
- Be aligned between the mobile library version & the desktop app version

---

## How to install the macOs app ?

MacOS may block the first launch of the application because it was not downloaded from the App Store. You'll need to manually authorize it through your system settings.

1. First, try to launch the app from the Applications folder. macOS will display a message stating that it cannot be opened.

<img src="https://github.com/user-attachments/assets/5317fa6c-4cdc-4582-bc3d-059e66b0713f" width="300"/>

2. Click on the `?` on top right of the dialog

<img src="https://github.com/user-attachments/assets/9d0e2f08-69a1-4510-9228-cdd6f9a77dd4" width="300"/>

3. On the system help page, click on the link "Open privacy & Security for me"

<img src="https://github.com/user-attachments/assets/aa2a40f7-2fd8-4243-be5e-8982c9260d1f" width="300"/>

4. Scroll down to the Security section. You should see a message mentioning the blocked application with an `Open Anyway` button.

<img src="https://github.com/user-attachments/assets/b5ba299c-4534-449b-9926-4075be8ba351" width="300"/>

5. Click on this button 😂

6. It opens again the first dialog, but with an additional button in the middle `Open Anyway`

<img src="https://github.com/user-attachments/assets/d5fb369d-3301-4489-8fc2-1ef6b0a1f52b" width="300"/>

7. Click on this button 😂 (it should ask you a password or fingerprint verification)

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
        <td align="center">
      <a href="https://github.com/doTTTTT">
        <img src="https://avatars.githubusercontent.com/u/13266870?v=4" width="100px;" alt="FRaphael Teyssandier"/><br />
        <sub><b>doTTTTT</b></sub>
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

## License

Flocon is MIT licensed, as found in the [LICENSE](/LICENSE) file.
