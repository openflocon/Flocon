---
title: Downloaded Image Viewer
description: Preview and inspect images fetched by your application in real time.
---

# 🖼️ Downloaded Image Viewer

Flocon captures and displays **images downloaded over the network** at runtime — such as user avatars, feed thumbnails, banners, and cached media assets.

---

## Overview

<img width="1297" height="838" alt="Image Previewer" src="https://github.com/user-attachments/assets/5f83ce95-0b03-4bfd-9d67-099c7b5ca5cc" style="border-radius: 8px;" />

For every downloaded image, Flocon displays:

- **Thumbnail Preview**: Direct visual preview rendered inside the desktop app
- **Format**: JPG, PNG, WEBP, GIF, SVG… shown as a badge on the image
- **Origin URL**: Full remote source address (host and path)
- **Size & Weight**: Decoded dimensions (e.g. `600×400`) and downloaded size (e.g. `142 KB`)
- **Timing**: Timestamp, images are listed in load order

Use the toolbar icons to switch between the **grid** (one card per image) and the **list** (one compact row per image).

Click an image to open it in the **full-screen viewer**:

- Browse with the arrows, the filmstrip at the bottom, or the `←` / `→` keys; `Esc` closes it
- Zoom with a trackpad pinch (macOS), `⌘`/`Ctrl` + mouse wheel, a double click, the `+` / `-` keys or the toolbar buttons; `0` goes back to fit
- When zoomed, scroll or drag to move around the image
- Copy the URL or open it in your browser from the toolbar

This is invaluable for debugging missing placeholders, CDN resolution failures, image compression artifacts, or unexpected network cache misses.

---

## Setup with Coil 3

When using Coil, connect your `ImageLoader` with the Flocon-enabled OkHttp or Ktor client:

```kotlin
SingletonImageLoader.setSafe {
    ImageLoader.Builder(context = context)
        .components {
            add(
                coil3.network.okhttp.OkHttpNetworkFetcherFactory(
                    callFactory = { okHttpClient }, // okHttpClient configured with FloconOkhttpInterceptor
                )
            )
        }
        .build()
}
```