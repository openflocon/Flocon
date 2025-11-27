### 📡 Network Request Inspector (kotlin multi platform compatible)

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

#### 🎭 HTTP Request Mocking (kotlin multi platform compatible)

<img width="1293" height="836" alt="Screenshot 2025-09-12 at 15 40 38" src="https://github.com/user-attachments/assets/3a529e3f-488e-4dba-aee1-fc6f70efcb08" />

Beyond simple inspection, Flocon now allows you to mock HTTP requests. This powerful feature gives you full control over your app's network layer without needing to change any code. You can intercept specific network calls and provide custom responses, making it easy to test various scenarios.

With this feature, you can:

- Simulate network errors: Test how your app handles different HTTP status codes (e.g., 404 Not Found, 500 Server Error).
- Create test data: Mock responses with specific data to test different UI states, even if your backend isn't ready yet.
- Create a new mock from an existing request, then test your app with some differences inside the prefious body
- Reduce dependencies: Develop and test features without needing a stable internet connection or a complete backend environment.

#### With OkHttp (android only)

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

#### With Ktor (kotlin multi platform compatible)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon-ktor-interceptor.svg)](https://search.maven.org/artifact/io.github.openflocon/flocon-ktor-interceptor)

tested with ktor `3.2.3`

```
debugImplementation("io.github.openflocon:flocon-ktor-interceptor:LAST_VERSION")
releaseImplementation("io.github.openflocon:flocon-ktor-interceptor-no-op:LAST_VERSION")
```

```kotlin
val httpClient = HttpClient(YourClient) { 
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

#### With OkHttp3 (android only)

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

#### 🧰 Manually (kotlin multi platform compatible)

If you are using other websockets libs than okhttp, you can easily forward events to FloconWebSocket

To log outgoing messages 
```kotlin
val message = "hello"

webSocket.send(message)

floconLogWebSocketEvent(
    FloconWebSocketEvent(
        websocketUrl = "ws://...",
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