# Swift: connection screen in a debug menu (optional)

Only when the user wants to connect a physical device without hard-coding the Mac IP. Prerequisite: [swift.md](swift.md).

## API semantics (FloconSwift `2.0.0`, checked at `2e822e1`)

| API | Behavior |
| :--- | :--- |
| `await Flocon.connect(address:)` | Returns a `FloconConnectResult` (`.connected`, `.notStarted`, `.invalidAddress(String)`, `.failed(String)`); never throws. One attempt, no retry: on failure the address is dropped. A new address replaces the previous extra one. |
| `await Flocon.disconnect()` | Closes the extra connection opened by `connect` and forgets its remembered address. The default connection (`localhost` or `config(host:)`) stays active and keeps retrying. There is no API to pause everything. |
| `Flocon.connectionState` / `Flocon.connectionStates` | **Global** state of all connections: `.connected` as soon as one is. The stream starts with the current value; use it instead of polling. |
| Address format | `host`, `host:wsPort`, `host:wsPort:httpPort`. No scheme, no path. Omitted ports are the configured ones (9023 / 9024 by default). |
| Remembered address | The last successfully connected extra address is saved in the app `UserDefaults` (`rememberLastAddress`, on by default) and tried at next launch; it wins over `flocon.connect { $0.address = … }` / `FloconConfig.additionalAddress`. |

Consequences for the UI:

- Show the result of the last attempt separately from the global status: with the Simulator connected through `localhost`, the status reads `.connected` even when the typed address failed.
- The text in the address field is not necessarily the connected address. Do not store a second copy of "the address" in the app: the SDK does not expose the remembered one at this revision, so label the field as "address to connect".
- `disconnect()` does not make the status leave `.connected` while the default connection is up.

## SwiftUI recipe

```swift
#if DEBUG && os(iOS)
import SwiftUI
import Flocon

struct FloconConnectionView: View {
    @State private var address = ""
    @State private var state: FloconConnectionState = .idle
    @State private var lastResult: String?
    @State private var isConnecting = false
    @State private var isScanning = false

    var body: some View {
        Form {
            Section("Status") {
                Text(describe(state))
            }
            Section("Connect to a Mac") {
                TextField("192.168.1.20 or host:wsPort:httpPort", text: $address)
                    .keyboardType(.numbersAndPunctuation)
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled()
                Button(isConnecting ? "Connecting…" : "Connect") {
                    Task { await connect() }
                }
                .disabled(isConnecting || address.trimmingCharacters(in: .whitespaces).isEmpty)
                Button("Disconnect this address", role: .destructive) {
                    Task {
                        await Flocon.disconnect()
                        lastResult = "Extra address forgotten (the default connection stays active)"
                    }
                }
                Button("Scan the QR code of Flocon Desktop") { isScanning = true }
                if let lastResult {
                    Text(lastResult).font(.footnote).foregroundStyle(.secondary)
                }
            }
        }
        .task {
            // cancelled with the view: no polling, no leak
            for await newState in Flocon.connectionStates {
                state = newState
            }
        }
        .sheet(isPresented: $isScanning) {
            QRSheet { result in
                lastResult = describe(result)
                if result == .connected { isScanning = false }
            }
        }
    }

    private func connect() async {
        isConnecting = true
        defer { isConnecting = false }
        let result = await Flocon.connect(address: address.trimmingCharacters(in: .whitespaces))
        lastResult = describe(result)
    }

    private func describe(_ state: FloconConnectionState) -> String {
        switch state {
        case .idle: "Not started"
        case .buffering: "Not connected, messages are kept"
        case .connecting: "Connecting…"
        case .connected: "Connected (at least one address)"
        case .failed(let reason): "Failed: \(reason)"
        }
    }

    private func describe(_ result: FloconConnectResult) -> String {
        switch result {
        case .connected: "Connected"
        case .notStarted: "Flocon is not started in this build"
        case .invalidAddress(let reason): "Invalid address: \(reason)"
        case .failed(let reason): "Connection failed: \(reason)"
        }
    }
}

private struct QRSheet: View {
    let onResult: @MainActor (FloconConnectResult) -> Void
    @State private var error: String?

    var body: some View {
        VStack {
            FloconQRScannerView { result in
                if case .connected = result { error = nil } else { error = "\(result)" }
                onResult(result)
            }
            if let error {
                Text(error).font(.footnote).foregroundStyle(.red).padding()
            }
        }
    }
}
#endif
```

From UIKit, host it with `UIHostingController(rootView: FloconConnectionView())`.

## QR scanner

- Reuse `FloconQRScannerView` (iOS only, AVFoundation, no dependency). It calls `Flocon.connect(address:)` itself and keeps scanning after a failure.
- `NSCameraUsageDescription` must be in the Info.plist; reuse the app's existing one if it has one. The view asks for permission on first display and shows a message when the camera is denied or absent.
- Show the failure inside the scan sheet, close it after success.
- The Simulator has no camera: never report the camera scan as tested from a Simulator. Test it on a device, or say it was not run.
- Physical device also needs `NSLocalNetworkUsageDescription`.

## Lifecycle

Tie observation and connection tasks to the screen (`.task {}` in SwiftUI, a stored `Task` cancelled in `viewDidDisappear` / `deinit` in UIKit). A `connect` already started finishes even if the screen closes; that is fine, the result is just not shown.

Gate the menu entry with the same condition as `startFlocon` ([swift.md](swift.md) section 3): in a build where Flocon is not started, `connect` returns `.notStarted`.
