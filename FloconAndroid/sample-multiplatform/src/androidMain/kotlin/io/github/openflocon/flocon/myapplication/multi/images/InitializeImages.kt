package io.github.openflocon.flocon.myapplication.multi.images

import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import okhttp3.OkHttpClient

fun initializeImages(context: Context, okHttpClient: OkHttpClient) {
    SingletonImageLoader.setSafe {
        ImageLoader.Builder(context = context)
            .components {
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
}
