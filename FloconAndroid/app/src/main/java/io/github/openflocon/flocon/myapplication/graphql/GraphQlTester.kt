package io.github.openflocon.flocon.myapplication.graphql

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.http.HttpRequest
import com.apollographql.apollo.api.http.HttpResponse
import com.apollographql.apollo.network.http.HttpInterceptor
import com.apollographql.apollo.network.http.HttpInterceptorChain
import com.github.GetUserInfoQuery
import io.github.openflocon.flocon.myapplication.BuildConfig
import io.github.openflocon.flocon.okhttp.FloconApolloInterceptor

object GraphQlTester {

    private const val GITHUB_TOKEN = BuildConfig.GITHUB_TOKEN

    val githubApolloClient by lazy {
        ApolloClient.Builder()
            .serverUrl("https://api.github.com/graphql")
            .addHttpInterceptor(object : HttpInterceptor {
                override suspend fun intercept(
                    request: HttpRequest,
                    chain: HttpInterceptorChain
                ): HttpResponse {
                    return chain.proceed(
                        request.newBuilder()
                            .addHeader("Authorization", "Bearer $GITHUB_TOKEN")
                            .build()
                    )
                }
            })
            .addHttpInterceptor(FloconApolloInterceptor()) // Ajoute votre intercepteur ici
            .build()
    }

    suspend fun fetchViewerInfo() {
        // Exécute la requête générée par Apollo
        val response = githubApolloClient
            .query(GetUserInfoQuery("florent37"))
            .execute()

        // Traite la réponse
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