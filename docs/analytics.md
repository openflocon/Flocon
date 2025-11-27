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
floconAnalytics("firebase").logEvents(
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