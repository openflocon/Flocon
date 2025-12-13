### 🛰️ GraphQL Request Inspector

Flocon also supports **GraphQL** requests via a dedicated Apollo interceptor.

Just like with REST, all outgoing GraphQL requests made through [Apollo Client](https://www.apollographql.com/docs/android/) are captured and displayed in Flocon’s interface — allowing you to debug your queries and mutations in real time.


For each GraphQL call, you can inspect:

- Response data or error payload
- Headers, status code, and response time
- The operation type (query / mutation)

```kotlin
ApolloClient.Builder()
            // just set your already configured with flocon okhttp interceptor client
            .okHttpClient(client)
            // regular builder methods
            .build()
```
