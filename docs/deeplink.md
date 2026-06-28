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
Flocon.deeplinks {
    variable("test_variable")
    variable("host") {
        description = "Host variable"
        autoComplete(listOf("flocon", "flocon2", "flocon3"))
    }

    deeplink("[host]://home") {
        "host" withVariable "host"
    }
    deeplink("[host]://test") {
        "host" withVariable "host"
    }
    deeplink("[host]://user/[userId]") {
        label = "User"
        "userId" withAutoComplete listOf("Florent", "David", "Guillaume")
        "host" withVariable "host"
    }
    deeplink("[host]://post/[postId]?comment=[commentText]") {
        label = "Post"
        description = "Open a post and send a comment"
        "postId" withAutoComplete listOf("1", "2", "3")
        "commentText" withVariable "test_variable"
        "host" withVariable "host"
    }
}
```