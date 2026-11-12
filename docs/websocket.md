---
title: WebSocket Inspector
description: Monitor and debug real-time WebSocket frames, messages, and events.
---

# 💬 WebSocket Inspector

Flocon captures **all WebSocket communications** made by your application, giving you complete visibility into live feeds, chats, and multiplayer events.

---

## Overview

<img width="1442" height="572" alt="WebSocket Inspection in Flocon" src="https://github.com/user-attachments/assets/49cef28f-87c9-4af7-a929-63d428d99f9e" style="border-radius: 8px;" />

For every active WebSocket connection, Flocon records:

- **Connection Lifecycle**: Connection URL, handshake status, opening timestamps, and closure codes
- **Frames**: Sent and received text frames, binary payloads, and ping/pong heartbeats
- **Order & Timestamps**: Precise sequential message ordering with millisecond timestamps

---

## Setup & Integration

WebSocket events are reported to the network plugin: add `flocon-network-core` and `install(FloconNetwork)` (see [Network](network.md#setup-integration)).

### With OkHttp (Android & JVM)

Flocon provides built-in WebSocket extension helpers for OkHttp (module `flocon-okhttp-interceptor`). Using the same `id` lets you send mocked messages to this WebSocket from the desktop app:

=== "Logging Outgoing Messages"

    ```kotlin
    // Sends the message and simultaneously logs it to Flocon
    webSocket.sendWithFlocon("\"$messagePayload\"")
    ```

=== "Logging Incoming Messages"

    ```kotlin
    val request = Request.Builder()
        .url("wss://your-websocket-endpoint.com")
        .build()

    val myListener = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            // Your handler
        }
    }

    // Wraps the listener to record all incoming frames in Flocon
    val webSocket = client.newWebSocket(
        request,
        myListener.listenWithFlocon(id = "wss://your-websocket-endpoint.com")
    )
    ```

---

### Manual Logging (Kotlin Multiplatform)

If you use custom WebSocket implementations (or multiplatform WebSocket engines), forward events directly to the network plugin:

=== "Outgoing Frames"

    ```kotlin
    Flocon.networkPlugin.logWebSocket(
        FloconWebSocketEvent(
            websocketUrl = "wss://api.example.com/ws",
            event = FloconWebSocketEvent.Event.SendMessage,
            message = outgoingText,
        )
    )
    ```

=== "Incoming Frames"

    ```kotlin
    Flocon.networkPlugin.logWebSocket(
        FloconWebSocketEvent(
            websocketUrl = "wss://api.example.com/ws",
            event = FloconWebSocketEvent.Event.ReceiveMessage,
            message = incomingText,
        )
    )
    ```
