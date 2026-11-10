---
title: Analytics Event Viewer
description: Inspect and validate real-time analytics events and custom telemetry streams.
---

# 📊 Analytics Event Viewer

Flocon streams and displays **analytics events** emitted by your application in real time, making it easy for developers, QA engineers, and product teams to verify tracking payloads.

---

## Overview

<img width="1296" height="837" alt="Analytics Events Stream" src="https://github.com/user-attachments/assets/e3f2a6ab-bf25-48ac-b9fe-8ea3f81206a1" style="border-radius: 8px;" />

<img width="1294" height="838" alt="Event Payload Details" src="https://github.com/user-attachments/assets/b7be4f8d-afcb-4bbc-8da4-c09e1cd240a6" style="border-radius: 8px; margin-top: 1rem;" />

Every recorded event details:
- **Event Name**: Unique tracking identifier
- **Parameters & Properties**: Full key-value dictionary
- **Timestamp & Source**: Accurate timestamp and stream category (e.g. Firebase, Segment, Custom)

---

## Usage

Log individual or batched events by stream name:

```kotlin
floconAnalytics("firebase").logEvents(
    AnalyticsEvent(
        eventName = "button_clicked",
        "button_id" analyticsProperty "checkout_pay",
        "cart_total" analyticsProperty "49.99",
        "currency" analyticsProperty "EUR"
    )
)
```

---

## Generic Analytics Forwarder

Forward all app tracking calls to Flocon alongside your existing provider:

```kotlin
class AnalyticsTracker(private val firebaseAnalytics: FirebaseAnalytics) {

    fun track(event: String, parameters: Map<String, Any> = emptyMap()) {
        // 1. Forward to Flocon Desktop
        floconAnalytics("app_telemetry").logEvents(
            AnalyticsEvent(
                eventName = event,
                parameters.map { it.key analyticsProperty it.value.toString() }
            )
        )

        // 2. Forward to Firebase
        val bundle = Bundle().apply {
            parameters.forEach { (k, v) -> putString(k, v.toString()) }
        }
        firebaseAnalytics.logEvent(event, bundle)
    }
}
```