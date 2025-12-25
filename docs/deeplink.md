### 🔗 Deeplink Launcher (android only)

<img width="1293" height="836" alt="Screenshot 2025-09-12 at 15 41 39" src="https://github.com/user-attachments/assets/eeaa30fb-6567-437a-96a4-dff44c6c6a54" />

Flocon includes a **deeplink runner**, which lists all the deeplinks supported by your app (either auto-discovered or manually registered).

From the desktop UI, you can:

- Browse available deeplinks
- Enter parameters interactively
- Execute deeplinks directly on the device
- Instantly navigate to specific app screens

No more typing long `adb shell am start` commands — Flocon makes deeplink testing accessible and efficient.

**You can configure deeplinks directly from your Android code!**

```kotlin
floconDeeplink {
    deeplink("flocon://home")
    deeplink("flocon://test")
    deeplink(
        "flocon://user/[userId]",
        label = "User",
        parameters = {
            "userId" withAutoComplete listOf("Florent", "David", "Guillaume")
        }
    )
    deeplink(
        "flocon://post/[postId]?comment=[commentText]",
        label = "Post",
        description = "Open a post and send a comment"
    )
}
```