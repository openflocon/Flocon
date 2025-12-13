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
