---
title: GraphQL Inspector
description: Debug Apollo GraphQL queries and mutations in real time with Flocon.
---

# 🛰️ GraphQL Request Inspector

Flocon supports **GraphQL** requests via Apollo Client, capturing and visualizing all queries, mutations, and subscriptions in real time.

---

## Capabilities

For each GraphQL request, Flocon inspects:
- **Operation Details**: Query / Mutation name, Operation type, and Variables payload
- **Response Payloads**: Formatted GraphQL response data or GraphQL error arrays
- **HTTP Transport**: Headers, HTTP status codes, latency, and transport timestamps

---

## Setup with Apollo Client

Apollo Client delegates network calls to OkHttp on Android. By attaching the `FloconOkhttpInterceptor` to the underlying HTTP client, GraphQL requests are automatically intercepted:

```kotlin
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(FloconOkhttpInterceptor())
    .build()

val apolloClient = ApolloClient.Builder()
    .serverUrl("https://your-api.com/graphql")
    .okHttpClient(okHttpClient)
    .build()
```
