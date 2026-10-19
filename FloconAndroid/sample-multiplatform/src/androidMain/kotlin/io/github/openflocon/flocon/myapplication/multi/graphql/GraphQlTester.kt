package io.github.openflocon.flocon.myapplication.multi.graphql

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.http.HttpRequest
import com.apollographql.apollo.api.http.HttpResponse
import com.apollographql.apollo.network.http.HttpInterceptor
import com.apollographql.apollo.network.http.HttpInterceptorChain
import com.apollographql.apollo.network.okHttpClient
import com.github.GetUserInfoQuery
import okhttp3.OkHttpClient

class GraphQlTester(
    val client: OkHttpClient,
    // a GitHub token, without it the calls are rejected (but still visible in Flocon)
    private val githubToken: String? = null,
) {

    val githubApolloClient by lazy {
        ApolloClient.Builder()
            .serverUrl("https://api.github.com/graphql")
            .okHttpClient(client)
            .addHttpInterceptor(object : HttpInterceptor {
                override suspend fun intercept(
                    request: HttpRequest,
                    chain: HttpInterceptorChain
                ): HttpResponse {
                    return chain.proceed(
                        request.newBuilder()
                            .apply {
                                if (!githubToken.isNullOrBlank()) {
                                    addHeader("Authorization", "Bearer $githubToken")
                                }
                            }
                            .build()
                    )
                }
            })
            .build()
    }

    suspend fun fetchViewerInfo() {
        val response = githubApolloClient
            .query(GetUserInfoQuery("florent37"))
            .execute()

        if (response.data != null) {
            val viewerName = response.data?.user?.name
            val avatarUrl = response.data?.user?.avatarUrl
            val bio = response.data?.user?.bio

            println("Nom : $viewerName, avatarUrl : $avatarUrl, bio : $bio")

        } else if (response.hasErrors()) {
            println("Erreurs: ${response.errors?.joinToString { it.message }}")
        }
    }
}
