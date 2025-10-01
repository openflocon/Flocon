package io.github.openflocon.flocon.okhttp

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

data class FloconNetworkIsImageParams(
    val request: Request,
    val response: Response,
    val responseContentType: String?,
)

class FloconOkhttpInterceptor(
    private val isImage: ((FloconNetworkIsImageParams) -> Boolean)? = null,
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}