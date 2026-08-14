---
title: "Tutorial: Network Mocking Step-by-Step"
description: Complete step-by-step tutorial on intercepting and mocking HTTP/REST network requests using Flocon.
---

# 🎭 Step-by-Step Tutorial: Network Mocking with Flocon

With Flocon, you can **intercept live HTTP requests and replace them with custom mock responses** directly from your desktop interface. 

You can test error handling (401 Unauthorized, 404 Not Found, 500 Internal Server Error), inject mock JSON data for unreleased backend features, or simulate slow network latency without writing test boilerplate or modifying your codebase.

---

## Prerequisites

Before starting, ensure you have:
1. Integrated the **Flocon SDK** into your Android or Kotlin Multiplatform project.
2. Downloaded and launched the **[Flocon Desktop App](https://github.com/openflocon/Flocon/releases)**.
3. Connected your test device or emulator (via USB debugging / ADB).

---

## Step 1: Install Network Interceptor

Add the appropriate network interceptor to your project depending on your networking library:

=== "With OkHttp (Android)"

    Add dependencies:
    ```kotlin
    dependencies {
        debugImplementation("io.github.openflocon:flocon-okhttp-interceptor:LAST_VERSION")
        releaseImplementation("io.github.openflocon:flocon-okhttp-interceptor-no-op:LAST_VERSION")
    }
    ```

    Attach `FloconOkhttpInterceptor` to your `OkHttpClient`:
    ```kotlin
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(FloconOkhttpInterceptor())
        .build()
    ```

=== "With Ktor (Kotlin Multiplatform)"

    Add dependencies in `commonMain`:
    ```kotlin
    kotlin {
        sourceSets {
            commonMain.dependencies {
                implementation("io.github.openflocon:flocon-ktor-interceptor:LAST_VERSION")
            }
        }
    }
    ```

    Install `FloconKtorPlugin` in your `HttpClient`:
    ```kotlin
    val httpClient = HttpClient {
        install(FloconKtorPlugin)
    }
    ```

---

## Step 2: Connect App to Flocon Desktop

1. Launch your mobile application on an emulator or physical device.
2. Open **Flocon Desktop** on your computer.
3. Verify that your device and application package name appear in the top-left device selector.

<!-- ========================================== -->
<!-- IMAGE PLACEHOLDER: STEP 2 - DEVICE CONNECTED -->
<!-- Description: Screenshot showing Flocon Desktop connected with device and app selected in the top bar -->
<!-- ========================================== -->
<div style="border: 2px dashed #6366f1; border-radius: 8px; padding: 2rem; text-align: center; margin: 1.5rem 0; background: rgba(99, 102, 241, 0.05);">
  <p style="margin: 0; font-weight: 600; color: #6366f1;">📸 Screenshot Placeholder: Device Connected</p>
  <p style="margin: 0.5rem 0 0 0; font-size: 0.9rem; color: gray;">
    Replace with screenshot of Flocon Desktop connected to your running app (e.g. <code>docs/assets/screenshots/mocking_step2_connected.png</code>)
  </p>
</div>

---

## Step 3: Trigger a Baseline Network Call

1. In your mobile application, perform an action that triggers a network request (e.g., loading a profile screen, fetching items).
2. In Flocon Desktop, open the **Network Inspector** tab.
3. You will see the live request appear in the list with its method, URL, status code, and response time.

<!-- ========================================== -->
<!-- IMAGE PLACEHOLDER: STEP 3 - REQUEST CAPTURED -->
<!-- Description: Screenshot of Network Inspector showing captured real API requests in the list -->
<!-- ========================================== -->
<div style="border: 2px dashed #6366f1; border-radius: 8px; padding: 2rem; text-align: center; margin: 1.5rem 0; background: rgba(99, 102, 241, 0.05);">
  <p style="margin: 0; font-weight: 600; color: #6366f1;">📸 Screenshot Placeholder: Captured Network Request</p>
  <p style="margin: 0.5rem 0 0 0; font-size: 0.9rem; color: gray;">
    Replace with screenshot of the Network Inspector table showing incoming requests (e.g. <code>docs/assets/screenshots/mocking_step3_request.png</code>)
  </p>
</div>

---

## Step 4: Create a Mock

There are two ways to create a mock rule:

### Option A: From an Existing Request (Recommended)
1. Select the captured request in the list.
2. In the right-hand details pane, click the **Create Mock** (or **Mock this Call**) button.
3. Flocon automatically populates the URL pattern, HTTP method, headers, and original JSON response body into the Mock Editor.

### Option B: From Scratch in the Mocks Manager
1. Navigate to the **Mocks** section in the navigation sidebar.
2. Click the **+ Add Mock** button to open a blank Mock Editor.

<!-- ========================================== -->
<!-- IMAGE PLACEHOLDER: STEP 4 - CREATE MOCK BUTTON -->
<!-- Description: Screenshot showing the "Create Mock" button highlighted on a selected request -->
<!-- ========================================== -->
<div style="border: 2px dashed #6366f1; border-radius: 8px; padding: 2rem; text-align: center; margin: 1.5rem 0; background: rgba(99, 102, 241, 0.05);">
  <p style="margin: 0; font-weight: 600; color: #6366f1;">📸 Screenshot Placeholder: Create Mock Button</p>
  <p style="margin: 0.5rem 0 0 0; font-size: 0.9rem; color: gray;">
    Replace with screenshot showing the "Create Mock" action button in the request detail view (e.g. <code>docs/assets/screenshots/mocking_step4_create_button.png</code>)
  </p>
</div>

---

## Step 5: Configure Mock Rules & Response

In the Mock Editor dialog, customize how the response should behave:

1. **Expectation Rules**:
    - **URL Match**: Enter the endpoint URL or URL pattern (supports wildcards/exact match, e.g. `https://api.example.com/users/*`).
    - **HTTP Method**: Choose `GET`, `POST`, `PUT`, `DELETE`, etc.
2. **Response Configuration**:
    - **HTTP Status Code**: Set the desired code (e.g., `200 OK`, `401 Unauthorized`, `500 Internal Server Error`).
    - **Response Body**: Edit the JSON payload directly with syntax highlighting.
    - **Response Delay (ms)**: Set an artificial delay (e.g., `1500 ms`) to test loading skeletons and progress spinners.
    - **Headers**: Add custom headers (e.g., `Content-Type: application/json` or custom authorization tokens).
3. Click **Save / Enable Mock**.

<!-- ========================================== -->
<!-- IMAGE PLACEHOLDER: STEP 5 - MOCK CONFIGURATION MODAL -->
<!-- Description: Screenshot of the Mock Editor modal with status code, delay, and edited JSON body -->
<!-- ========================================== -->
<div style="border: 2px dashed #6366f1; border-radius: 8px; padding: 2rem; text-align: center; margin: 1.5rem 0; background: rgba(99, 102, 241, 0.05);">
  <p style="margin: 0; font-weight: 600; color: #6366f1;">📸 Screenshot Placeholder: Mock Editor Configuration</p>
  <p style="margin: 0.5rem 0 0 0; font-size: 0.9rem; color: gray;">
    Replace with screenshot of the Mock Editor dialog showing configured status code, delay, and JSON payload (e.g. <code>docs/assets/screenshots/mocking_step5_editor.png</code>)
  </p>
</div>

---

## Step 6: Trigger the Call & Observe the Result

1. Return to your mobile application.
2. Re-trigger the same action (e.g., pull to refresh or re-open the screen).
3. **In the mobile app**: The app receives the mock response immediately without contacting the backend server!
4. **In Flocon Desktop**: The request is logged in the Network Inspector with a visible **`[MOCKED]`** tag and purple badge.

<!-- ========================================== -->
<!-- IMAGE PLACEHOLDER: STEP 6 - MOCKED REQUEST IN DESKTOP -->
<!-- Description: Screenshot showing the intercepted request marked with the [MOCKED] badge in the event table -->
<!-- ========================================== -->
<div style="border: 2px dashed #6366f1; border-radius: 8px; padding: 2rem; text-align: center; margin: 1.5rem 0; background: rgba(99, 102, 241, 0.05);">
  <p style="margin: 0; font-weight: 600; color: #6366f1;">📸 Screenshot Placeholder: Intercepted Mock in Event Log</p>
  <p style="margin: 0.5rem 0 0 0; font-size: 0.9rem; color: gray;">
    Replace with screenshot of the Network Inspector showing the `[MOCKED]` badge and response payload (e.g. <code>docs/assets/screenshots/mocking_step6_mocked_result.png</code>)
  </p>
</div>

---

## Step 7: Advanced Mocking Features

### 1. Simulating Network Errors & Connection Drops
Instead of returning a JSON body with a 500 code, you can simulate a hard network failure (e.g. `IOException` / Socket Timeout). In the Mock Editor, select **Error / Exception** mode to test retry mechanisms and offline fallback screens.

### 2. Live Enable / Disable Toggling
In the **Mocks Manager** list, each mock has an **Enabled / Disabled** switch. Toggle mocks on and off instantly without deleting your configuration.

### 3. Export & Import Mock Profiles
Save your mock suites to `.json` files to:
- Share test scenarios with other developers on your team.
- Attach mock scenarios to Jira / GitHub bug reports for QA reproduction.
- Switch between different test environments (e.g., Happy Path, Edge Cases, Outage Simulation).

<!-- ========================================== -->
<!-- IMAGE PLACEHOLDER: STEP 7 - MOCKS LIST & EXPORT -->
<!-- Description: Screenshot of the Mocks list with toggles and the Export/Import buttons -->
<!-- ========================================== -->
<div style="border: 2px dashed #6366f1; border-radius: 8px; padding: 2rem; text-align: center; margin: 1.5rem 0; background: rgba(99, 102, 241, 0.05);">
  <p style="margin: 0; font-weight: 600; color: #6366f1;">📸 Screenshot Placeholder: Mocks List with Toggle & Export</p>
  <p style="margin: 0.5rem 0 0 0; font-size: 0.9rem; color: gray;">
    Replace with screenshot of the Mocks list view showing toggle switches and export/import options (e.g. <code>docs/assets/screenshots/mocking_step7_mocks_list.png</code>)
  </p>
</div>

---

## Summary Checklist

| Action | Where |
| :--- | :--- |
| **1. Install Interceptor** | Add `FloconOkhttpInterceptor` (OkHttp) or `FloconKtorPlugin` (Ktor) |
| **2. Connect App** | Ensure device appears in Flocon Desktop top bar |
| **3. Capture Request** | Trigger request from mobile app |
| **4. Create Mock** | Click **Create Mock** on captured request or click **+ Add Mock** |
| **5. Customize Payload** | Modify status code, response body, latency delay |
| **6. Verify** | Re-run in mobile app and look for the `[MOCKED]` indicator |
