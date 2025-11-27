### 🖼️ Downloaded Image Viewer

<img width="1297" height="838" alt="Screenshot 2025-09-12 at 15 40 53" src="https://github.com/user-attachments/assets/5f83ce95-0b03-4bfd-9d67-099c7b5ca5cc" />

Flocon captures and displays **images downloaded by the Android app**, giving you a clear, visual representation of media fetched over the network — such as avatars, product thumbnails, banners, or any other images requested at runtime.

For each image, Flocon shows:

- A live **thumbnail preview** of the image  
- The **URL** from which it was downloaded
- The **download timestamp**  

This feature is extremely useful for:

- Verifying that images are loading correctly and not broken  
- Debugging CDN issues, placeholders, or misconfigured URLs  
- Comparing image quality and compression at runtime  
- Inspecting lazy loading or image caching behaviors  

Whether you're working on UI/UX, performance optimization, or just debugging a missing image, this tool gives you **immediate visibility** into every image fetched by your app.

Usage with coil

```kotlin
// just add your okhttp client (with the flocon interceptor)
SingletonImageLoader.setSafe {
        ImageLoader.Builder(context = context)
            .components {
                // works also for ktor network fetcher
                add(
                    coil3.network.okhttp.OkHttpNetworkFetcherFactory(
                        callFactory = {
                            okHttpClient
                        },
                    ),
                )
            }
            .build()
}
```