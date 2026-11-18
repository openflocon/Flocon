package com.flocon.data.remote.mock.scenarios

import com.flocon.data.remote.mock.MockDeviceBuilder
import com.flocon.data.remote.mock.ago
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

// Network-flavoured data of the shopping app for the `doc-graphql`, `doc-websocket` and `doc-grpc` scenarios.

private const val REST_API = "https://api.flocon-shop.com/v1"
private const val GRAPHQL_URL = "https://api.flocon-shop.com/graphql"
private const val GRPC_AUTHORITY = "grpc.flocon-shop.com:443"
private const val JSON_TYPE = "application/json"

private fun String.jsonEscaped() = replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")

/** One GraphQL operation: the body has the `query`, `operationName` and `variables` keys the desktop detects. */
private fun MockDeviceBuilder.graphQl(
    at: Duration,
    operationName: String,
    query: String,
    variables: String,
    response: String,
    duration: Duration,
    status: Int = 200,
) {
    network(at, "POST", GRAPHQL_URL, status = status, duration = duration) {
        requestHeader("content-type", JSON_TYPE)
        requestHeader("authorization", "Bearer eyJhbGciOiJSUzI1NiJ9.sample.signature")
        requestHeader("x-apollo-operation-name", operationName)
        requestBody("""{"operationName":"$operationName","query":"${query.jsonEscaped()}","variables":$variables}""")
        responseHeader("content-type", JSON_TYPE)
        responseBody(response)
    }
}

/** GraphQL calls to a single `/graphql` endpoint: queries, mutations, two with an `errors` array and one HTTP error. */
internal fun MockDeviceBuilder.sampleGraphQlCalls() {
    graphQl(
        at = 11.minutes.ago,
        operationName = "GetHome",
        query = "query GetHome {\n  banners { id title imageUrl }\n  featuredProducts(limit: 4) { id name price }\n}",
        variables = "{}",
        response = """{"data":{"banners":[{"id":"b-1","title":"Summer sale","imageUrl":"https://images.unsplash.com/photo-1542291026-7eec264c27ff"}],"featuredProducts":[{"id":"1007","name":"Trail Runner 3","price":129.9},{"id":"1011","name":"Daily Sneaker","price":89.9},{"id":"1020","name":"Rain Boot","price":74.9},{"id":"1033","name":"Wool Socks","price":12.5}]}}""",
        duration = 212.milliseconds,
    )
    graphQl(
        at = 10.minutes.ago + 20.seconds,
        operationName = "GetProducts",
        query = "query GetProducts(${'$'}category: String!, ${'$'}first: Int!, ${'$'}after: String) {\n  products(category: ${'$'}category, first: ${'$'}first, after: ${'$'}after) {\n    edges { node { id name price currency rating } }\n    pageInfo { hasNextPage endCursor }\n  }\n}",
        variables = """{"category":"shoes","first":20,"after":null}""",
        response = """{"data":{"products":{"edges":[{"node":{"id":"1007","name":"Trail Runner 3","price":129.9,"currency":"EUR","rating":4.6}},{"node":{"id":"1011","name":"Daily Sneaker","price":89.9,"currency":"EUR","rating":4.3}},{"node":{"id":"1014","name":"City Loafer","price":109.0,"currency":"EUR","rating":4.1}}],"pageInfo":{"hasNextPage":true,"endCursor":"Y3Vyc29yOjIw"}}}}""",
        duration = 341.milliseconds,
    )
    graphQl(
        at = 9.minutes.ago + 30.seconds,
        operationName = "GetProducts",
        query = "query GetProducts(${'$'}category: String!, ${'$'}first: Int!, ${'$'}after: String) {\n  products(category: ${'$'}category, first: ${'$'}first, after: ${'$'}after) {\n    edges { node { id name price currency rating } }\n    pageInfo { hasNextPage endCursor }\n  }\n}",
        variables = """{"category":"shoes","first":20,"after":"Y3Vyc29yOjIw"}""",
        response = """{"data":{"products":{"edges":[{"node":{"id":"1020","name":"Rain Boot","price":74.9,"currency":"EUR","rating":4.0}},{"node":{"id":"1024","name":"Court Classic","price":99.0,"currency":"EUR","rating":4.4}}],"pageInfo":{"hasNextPage":false,"endCursor":"Y3Vyc29yOjQw"}}}}""",
        duration = 287.milliseconds,
    )
    graphQl(
        at = 9.minutes.ago,
        operationName = "GetProductDetails",
        query = "query GetProductDetails(${'$'}id: ID!) {\n  product(id: ${'$'}id) {\n    id name description price stock\n    sizes { label available }\n    reviews(first: 3) { author rating comment }\n  }\n}",
        variables = """{"id":"1007"}""",
        response = """{"data":{"product":{"id":"1007","name":"Trail Runner 3","description":"Lightweight trail running shoe with a grippy outsole.","price":129.9,"stock":12,"sizes":[{"label":"41","available":true},{"label":"42","available":true},{"label":"43","available":false}],"reviews":[{"author":"Marie","rating":5,"comment":"Super confortable"},{"author":"Karim","rating":4,"comment":"Runs a bit small"}]}}}""",
        duration = 156.milliseconds,
    )
    graphQl(
        at = 8.minutes.ago + 15.seconds,
        operationName = "SearchProducts",
        query = "query SearchProducts(${'$'}term: String!) {\n  search(term: ${'$'}term) { id name price }\n}",
        variables = """{"term":"trail"}""",
        response = """{"data":{"search":[{"id":"1007","name":"Trail Runner 3","price":129.9},{"id":"1008","name":"Trail Runner 3 GTX","price":149.9}]}}""",
        duration = 98.milliseconds,
    )
    graphQl(
        at = 8.minutes.ago,
        operationName = "AddToCart",
        query = "mutation AddToCart(${'$'}input: AddToCartInput!) {\n  addToCart(input: ${'$'}input) {\n    cart { id itemCount total }\n  }\n}",
        variables = """{"input":{"productId":"1007","quantity":1,"size":"42"}}""",
        response = """{"data":{"addToCart":{"cart":{"id":"c-981","itemCount":1,"total":129.9}}}}""",
        duration = 176.milliseconds,
    )
    graphQl(
        at = 7.minutes.ago + 10.seconds,
        operationName = "GetCart",
        query = "query GetCart {\n  cart { id itemCount total items { product { id name } quantity size unitPrice } }\n}",
        variables = "{}",
        response = """{"data":{"cart":{"id":"c-981","itemCount":1,"total":129.9,"items":[{"product":{"id":"1007","name":"Trail Runner 3"},"quantity":1,"size":"42","unitPrice":129.9}]}}}""",
        duration = 121.milliseconds,
    )
    graphQl(
        at = 6.minutes.ago + 30.seconds,
        operationName = "ApplyPromoCode",
        query = "mutation ApplyPromoCode(${'$'}code: String!) {\n  applyPromoCode(code: ${'$'}code) { cart { id total } discount }\n}",
        variables = """{"code":"SUMMER26"}""",
        response = """{"errors":[{"message":"Promo code SUMMER26 has expired","path":["applyPromoCode"],"extensions":{"code":"PROMO_EXPIRED","expiredAt":"2026-09-30"}}],"data":{"applyPromoCode":null}}""",
        duration = 134.milliseconds,
    )
    graphQl(
        at = 6.minutes.ago,
        operationName = "ApplyPromoCode",
        query = "mutation ApplyPromoCode(${'$'}code: String!) {\n  applyPromoCode(code: ${'$'}code) { cart { id total } discount }\n}",
        variables = """{"code":"WELCOME10"}""",
        response = """{"data":{"applyPromoCode":{"cart":{"id":"c-981","total":116.91},"discount":12.99}}}""",
        duration = 142.milliseconds,
    )
    graphQl(
        at = 5.minutes.ago,
        operationName = "GetShippingOptions",
        query = "query GetShippingOptions(${'$'}zip: String!) {\n  shippingOptions(zip: ${'$'}zip) { id label price estimatedDays }\n}",
        variables = """{"zip":"75011"}""",
        response = """{"data":{"shippingOptions":[{"id":"standard","label":"Standard","price":0.0,"estimatedDays":4},{"id":"express","label":"Express","price":7.9,"estimatedDays":1}]}}""",
        duration = 109.milliseconds,
    )
    graphQl(
        at = 4.minutes.ago,
        operationName = "PlaceOrder",
        query = "mutation PlaceOrder(${'$'}input: PlaceOrderInput!) {\n  placeOrder(input: ${'$'}input) { order { id status total } }\n}",
        variables = """{"input":{"cartId":"c-981","paymentMethodId":"pm_card_1","shippingOptionId":"standard"}}""",
        response = """{"errors":[{"message":"Payment declined by the issuer","path":["placeOrder"],"extensions":{"code":"PAYMENT_DECLINED"}},{"message":"Cart c-981 is locked","path":["placeOrder","order"],"extensions":{"code":"CART_LOCKED"}}],"data":null}""",
        duration = 1_340.milliseconds,
    )
    graphQl(
        at = 3.minutes.ago,
        operationName = "PlaceOrder",
        query = "mutation PlaceOrder(${'$'}input: PlaceOrderInput!) {\n  placeOrder(input: ${'$'}input) { order { id status total } }\n}",
        variables = """{"input":{"cartId":"c-981","paymentMethodId":"pm_card_2","shippingOptionId":"standard"}}""",
        response = """{"data":{"placeOrder":{"order":{"id":"o-5521","status":"CONFIRMED","total":116.91}}}}""",
        duration = 902.milliseconds,
    )
    graphQl(
        at = 2.minutes.ago,
        operationName = "GetOrders",
        query = "query GetOrders(${'$'}first: Int!) {\n  me { orders(first: ${'$'}first) { id status total createdAt } }\n}",
        variables = """{"first":10}""",
        response = """{"errors":[{"message":"Internal server error","extensions":{"code":"INTERNAL_SERVER_ERROR","requestId":"req-7f3c2a"}}]}""",
        duration = 3_020.milliseconds,
        status = 503,
    )
}

private const val CHAT_URL = "wss://ws.flocon-shop.com/support/chat"
private const val PRICES_URL = "wss://ws.flocon-shop.com/live/prices"

/** A few HTTP calls and a lot of WebSocket events on two sockets: order tracking and support chat, then live prices. */
internal fun MockDeviceBuilder.sampleWebSocketSession() {
    network(9.minutes.ago, "GET", "$REST_API/config", duration = 84.milliseconds) {
        responseHeader("content-type", JSON_TYPE)
        responseBody("""{"minVersion":"2.1.0","maintenance":false,"websocket":"$CHAT_URL"}""")
    }
    network(8.minutes.ago + 30.seconds, "GET", "$REST_API/orders/o-5521", duration = 118.milliseconds) {
        responseHeader("content-type", JSON_TYPE)
        responseBody("""{"id":"o-5521","status":"confirmed","total":116.91,"items":[{"productId":1007,"name":"Trail Runner 3","quantity":1}]}""")
    }
    network(8.minutes.ago, "POST", "$REST_API/support/tickets", status = 201, duration = 205.milliseconds) {
        requestHeader("content-type", JSON_TYPE)
        requestBody("""{"orderId":"o-5521","subject":"Where is my order?"}""")
        responseHeader("content-type", JSON_TYPE)
        responseBody("""{"ticketId":"t-318","status":"open","chatUrl":"$CHAT_URL"}""")
    }
    network(30.seconds.ago, "POST", "$REST_API/support/tickets/t-318/close", duration = 96.milliseconds) {
        requestHeader("content-type", JSON_TYPE)
        responseHeader("content-type", JSON_TYPE)
        responseBody("""{"ticketId":"t-318","status":"closed"}""")
    }

    // support chat + order tracking on the same socket
    webSocket(7.minutes.ago + 30.seconds, CHAT_URL, "open")
    webSocket(7.minutes.ago + 29.seconds, CHAT_URL, "sent", """{"type":"auth","token":"eyJhbGciOiJSUzI1NiJ9.sample.signature","ticketId":"t-318"}""")
    webSocket(7.minutes.ago + 28.seconds, CHAT_URL, "received", """{"type":"auth_ok","sessionId":"s-8841","agent":null}""")
    webSocket(7.minutes.ago + 25.seconds, CHAT_URL, "sent", """{"type":"subscribe","channel":"order.o-5521"}""")
    webSocket(7.minutes.ago + 24.seconds, CHAT_URL, "received", """{"type":"subscribed","channel":"order.o-5521","status":"confirmed"}""")
    webSocket(7.minutes.ago + 10.seconds, CHAT_URL, "received", """{"type":"agent_joined","agent":{"id":"a-12","name":"Sophie"}}""")
    webSocket(7.minutes.ago, CHAT_URL, "received", """{"type":"message","from":"agent","text":"Hi Jane, I'm Sophie. How can I help?","sentAt":"2026-11-12T10:41:02Z"}""")
    webSocket(6.minutes.ago + 40.seconds, CHAT_URL, "sent", """{"type":"message","text":"Hello! My order o-5521 hasn't shipped yet."}""")
    webSocket(6.minutes.ago + 38.seconds, CHAT_URL, "received", """{"type":"typing","from":"agent"}""")
    webSocket(6.minutes.ago + 25.seconds, CHAT_URL, "received", """{"type":"message","from":"agent","text":"Let me check. One moment please.","sentAt":"2026-11-12T10:41:45Z"}""")
    webSocket(6.minutes.ago + 5.seconds, CHAT_URL, "received", """{"type":"order_update","orderId":"o-5521","status":"packed","updatedAt":"2026-11-12T10:42:05Z"}""")
    webSocket(6.minutes.ago, CHAT_URL, "received", """{"type":"message","from":"agent","text":"Your parcel has just been packed, it leaves the warehouse today.","sentAt":"2026-11-12T10:42:10Z"}""")
    webSocket(5.minutes.ago + 40.seconds, CHAT_URL, "sent", """{"type":"message","text":"Great, thanks! Can I get a tracking number?"}""")
    webSocket(5.minutes.ago + 20.seconds, CHAT_URL, "received", """{"type":"message","from":"agent","text":"Sure: FR-7733-2201-AB","sentAt":"2026-11-12T10:42:50Z"}""")
    webSocket(5.minutes.ago + 19.seconds, CHAT_URL, "sent", """{"type":"read","upTo":"m-9"}""")
    webSocket(5.minutes.ago, CHAT_URL, "received", """{"type":"ping","id":12}""")
    webSocket(5.minutes.ago, CHAT_URL, "sent", """{"type":"pong","id":12}""")
    webSocket(4.minutes.ago + 50.seconds, CHAT_URL, "received", """{"type":"order_update","orderId":"o-5521","status":"shipped","carrier":"Colissimo","trackingNumber":"FR-7733-2201-AB"}""")
    webSocket(4.minutes.ago + 20.seconds, CHAT_URL, "received", """{"type":"order_update","orderId":"o-5521","status":"in_transit","location":"Paris hub"}""")
    webSocket(4.minutes.ago, CHAT_URL, "received", """{"type":"ping","id":13}""")
    webSocket(4.minutes.ago, CHAT_URL, "sent", """{"type":"pong","id":13}""")

    // the connection drops and comes back
    webSocket(3.minutes.ago + 30.seconds, CHAT_URL, "error", error = "java.net.SocketException: Connection reset")
    webSocket(3.minutes.ago + 30.seconds, CHAT_URL, "closed")
    webSocket(3.minutes.ago + 25.seconds, CHAT_URL, "open")
    webSocket(3.minutes.ago + 25.seconds, CHAT_URL, "sent", """{"type":"auth","token":"eyJhbGciOiJSUzI1NiJ9.sample.signature","ticketId":"t-318","resumeFrom":"m-9"}""")
    webSocket(3.minutes.ago + 24.seconds, CHAT_URL, "received", """{"type":"auth_ok","sessionId":"s-8842","agent":{"id":"a-12","name":"Sophie"}}""")
    webSocket(3.minutes.ago, CHAT_URL, "received", """{"type":"order_update","orderId":"o-5521","status":"out_for_delivery","eta":"2026-11-12T16:00:00Z"}""")
    webSocket(2.minutes.ago + 30.seconds, CHAT_URL, "received", """{"type":"message","from":"agent","text":"Your order is out for delivery, expect it this afternoon!","sentAt":"2026-11-12T10:50:00Z"}""")
    webSocket(2.minutes.ago + 10.seconds, CHAT_URL, "sent", """{"type":"message","text":"Perfect, thank you Sophie!"}""")
    webSocket(2.minutes.ago, CHAT_URL, "sent", """{"type":"unsubscribe","channel":"order.o-5521"}""")

    // live prices: a stream of small ticks
    webSocket(100.seconds.ago, PRICES_URL, "open")
    webSocket(99.seconds.ago, PRICES_URL, "sent", """{"type":"subscribe","symbols":["EURUSD","EURGBP","BTCEUR"]}""")
    val prices = listOf("EURUSD" to 1.0852, "EURGBP" to 0.8641, "BTCEUR" to 61_240.5)
    repeat(24) { tick ->
        val (symbol, base) = prices[tick % prices.size]
        val price = base + ((tick * 7) % 11 - 5) * base * 0.0002
        webSocket(
            (95 - tick * 3).seconds.ago,
            PRICES_URL,
            "received",
            """{"type":"tick","symbol":"$symbol","price":${"%.4f".format(java.util.Locale.ROOT, price)},"seq":${1000 + tick}}""",
        )
    }
    webSocket(20.seconds.ago, PRICES_URL, "sent", """{"type":"unsubscribe","symbols":["BTCEUR"]}""")
    webSocket(15.seconds.ago, PRICES_URL, "closing")
    webSocket(14.seconds.ago, PRICES_URL, "closed")
    webSocket(10.seconds.ago, CHAT_URL, "closing")
    webSocket(9.seconds.ago, CHAT_URL, "closed")
}

/** gRPC calls on a few services (unary calls, metadata, JSON messages): OK, an UNAVAILABLE, a DEADLINE_EXCEEDED, and two plain GET calls. */
internal fun MockDeviceBuilder.sampleGrpcCalls() {
    fun grpcCall(
        at: Duration,
        method: String,
        request: String,
        response: String,
        duration: Duration,
        status: String = "OK",
        extraMetadata: Pair<String, String>? = null,
    ) {
        grpc(at, GRPC_AUTHORITY, method, grpcStatus = status, duration = duration) {
            requestHeader("content-type", "application/grpc")
            requestHeader("authorization", "Bearer eyJhbGciOiJSUzI1NiJ9.sample.signature")
            requestHeader("user-agent", "grpc-kotlin-okhttp/1.4.1")
            requestHeader("x-request-id", "req-${at.inWholeSeconds}")
            extraMetadata?.let { requestHeader(it.first, it.second) }
            requestBody(request)
            responseHeader("content-type", "application/grpc")
            responseHeader("grpc-encoding", "gzip")
            responseHeader("grpc-status", status)
            responseBody(response)
        }
    }
    network(9.minutes.ago, "GET", "$REST_API/config", duration = 84.milliseconds) {
        responseHeader("content-type", JSON_TYPE)
        responseBody("""{"minVersion":"2.1.0","maintenance":false,"grpc":"$GRPC_AUTHORITY"}""")
    }
    grpcCall(
        8.minutes.ago + 40.seconds,
        "shop.auth.AuthService/Login",
        """{"email":"jane.doe@example.com","password":"••••••••"}""",
        """{"accessToken":"eyJhbGciOiJSUzI1NiJ9.sample.signature","expiresInSeconds":3600,"userId":42}""",
        230.milliseconds,
    )
    grpcCall(
        8.minutes.ago + 20.seconds,
        "shop.user.UserService/GetProfile",
        """{"userId":42}""",
        """{"id":42,"name":"Jane Doe","email":"jane.doe@example.com","premium":true,"addresses":[{"city":"Paris","zip":"75011"}]}""",
        64.milliseconds,
    )
    grpcCall(
        7.minutes.ago + 30.seconds,
        "shop.catalog.CatalogService/ListProducts",
        """{"category":"shoes","pageSize":20,"pageToken":""}""",
        """{"products":[{"id":1007,"name":"Trail Runner 3","priceCents":12990},{"id":1011,"name":"Daily Sneaker","priceCents":8990},{"id":1014,"name":"City Loafer","priceCents":10900}],"nextPageToken":"20"}""",
        182.milliseconds,
    )
    grpcCall(
        7.minutes.ago,
        "shop.catalog.CatalogService/GetProduct",
        """{"id":1007}""",
        """{"id":1007,"name":"Trail Runner 3","description":"Lightweight trail running shoe","priceCents":12990,"stock":12,"rating":4.6}""",
        71.milliseconds,
    )
    grpcCall(
        6.minutes.ago,
        "shop.catalog.CatalogService/SearchProducts",
        """{"query":"trail","limit":10}""",
        """{"products":[{"id":1007,"name":"Trail Runner 3"},{"id":1008,"name":"Trail Runner 3 GTX"}]}""",
        95.milliseconds,
    )
    grpcCall(
        5.minutes.ago + 20.seconds,
        "shop.cart.CartService/AddItem",
        """{"cartId":"c-981","productId":1007,"quantity":1,"size":"42"}""",
        """{"cartId":"c-981","itemCount":1,"totalCents":12990}""",
        58.milliseconds,
        extraMetadata = "x-idempotency-key" to "9b2f6c1e-7a3d-4e55-b0a2-1c5d8e4f3a10",
    )
    grpcCall(
        4.minutes.ago + 30.seconds,
        "shop.cart.CartService/GetCart",
        """{"cartId":"c-981"}""",
        """{"cartId":"c-981","items":[{"productId":1007,"quantity":1,"unitPriceCents":12990}],"totalCents":12990}""",
        47.milliseconds,
    )
    grpcCall(
        3.minutes.ago + 40.seconds,
        "shop.payment.PaymentService/Charge",
        """{"cartId":"c-981","paymentMethodId":"pm_card_1","amountCents":12990,"currency":"EUR"}""",
        """{"code":14,"message":"upstream connect error or disconnect/reset before headers. reset reason: connection failure"}""",
        2_100.milliseconds,
        status = "UNAVAILABLE",
        extraMetadata = "x-idempotency-key" to "2d8e91aa-4c0b-4f3e-9a7d-55e1b6c0f7a2",
    )
    grpcCall(
        3.minutes.ago,
        "shop.payment.PaymentService/Charge",
        """{"cartId":"c-981","paymentMethodId":"pm_card_1","amountCents":12990,"currency":"EUR"}""",
        """{"code":4,"message":"Deadline exceeded after 5.000s"}""",
        5_003.milliseconds,
        status = "DEADLINE_EXCEEDED",
        extraMetadata = "grpc-timeout" to "5S",
    )
    grpcCall(
        2.minutes.ago,
        "shop.payment.PaymentService/Charge",
        """{"cartId":"c-981","paymentMethodId":"pm_card_1","amountCents":12990,"currency":"EUR"}""",
        """{"chargeId":"ch_3PqXyZ","status":"SUCCEEDED","amountCents":12990}""",
        612.milliseconds,
        extraMetadata = "x-idempotency-key" to "2d8e91aa-4c0b-4f3e-9a7d-55e1b6c0f7a2",
    )
    grpcCall(
        100.seconds.ago,
        "shop.orders.OrderService/CreateOrder",
        """{"cartId":"c-981","chargeId":"ch_3PqXyZ","shippingOption":"standard"}""",
        """{"orderId":"o-5521","status":"CONFIRMED","totalCents":12990}""",
        264.milliseconds,
    )
    grpcCall(
        60.seconds.ago,
        "shop.orders.OrderService/ListOrders",
        """{"userId":42,"pageSize":10}""",
        """{"orders":[{"orderId":"o-5521","status":"CONFIRMED","totalCents":12990},{"orderId":"o-5410","status":"DELIVERED","totalCents":5990}]}""",
        88.milliseconds,
    )
    network(30.seconds.ago, "GET", "$REST_API/orders/o-5521/tracking", duration = 143.milliseconds) {
        responseHeader("content-type", JSON_TYPE)
        responseBody("""{"orderId":"o-5521","carrier":"Colissimo","status":"in_transit","eta":"2026-11-14"}""")
    }
}

/**
 * A 2.5 minutes session built for the timeline list and graph: the lanes App (start line), Network, Analytics, Logs
 * and Database are all filled, calls overlap, and a crash ends it. Use with `appStartedAt = 170.seconds.ago`.
 */
internal fun MockDeviceBuilder.sampleRichTimelineSession() {
    fun api(at: Duration, method: String, path: String, ms: Long, status: Int = 200, request: String? = null, response: String = """{"ok":true}""") {
        network(at, method, "$REST_API$path", status = status, duration = ms.milliseconds) {
            request?.let {
                requestHeader("content-type", JSON_TYPE)
                requestBody(it)
            }
            responseHeader("content-type", JSON_TYPE)
            responseBody(response)
        }
    }

    // launch
    timeline(168.seconds.ago, "Application", type = "lifecycle", level = null, params = mapOf("event" to "onCreate"))
    timeline(167.seconds.ago, "SplashScreen", type = "navigation", level = null)
    analytics(167.seconds.ago, "app_open", "source" to "launcher", "cold_start" to "true")
    api(166.seconds.ago, "GET", "/config", 420, response = """{"minVersion":"2.1.0","maintenance":false}""")
    api(165.seconds.ago, "GET", "/feature-flags", 610, response = """{"checkout_v2":true,"reviews":true}""")
    api(165.seconds.ago, "GET", "/users/me", 380, response = """{"id":42,"name":"Jane Doe"}""")
    databaseQuery(164.seconds.ago, "app_database", "SELECT * FROM users WHERE id = ?", "42")
    timeline(163.seconds.ago, "HomeScreen", type = "navigation", level = null, params = mapOf("from" to "SplashScreen"))
    analytics(163.seconds.ago, "screen_view", "screen" to "home")
    api(162.seconds.ago, "GET", "/home/banners", 540, response = """{"banners":[{"id":"b-1","title":"Summer sale"}]}""")
    api(162.seconds.ago, "GET", "/products/featured", 1_250, response = """{"items":[{"id":1007,"name":"Trail Runner 3"}]}""")
    network(161.seconds.ago, "GET", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&h=400&fit=crop&fm=jpg", duration = 740.milliseconds) {
        responseHeader("content-type", "image/jpeg")
        responseSize(184_320)
    }
    network(161.seconds.ago, "GET", "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=400&h=400&fit=crop&fm=jpg", duration = 520.milliseconds) {
        responseHeader("content-type", "image/jpeg")
        responseSize(52_224)
    }
    databaseQuery(159.seconds.ago, "app_database", "SELECT * FROM products ORDER BY popularity DESC LIMIT ?", "20")
    databaseQuery(159.seconds.ago, "cache.db", "INSERT OR REPLACE INTO http_cache (url, etag, size_bytes, fetched_at) VALUES (?, ?, ?, ?)", "$REST_API/products/featured", "\"a1b2c3\"", "4870", "1761950000000")

    // catalog
    timeline(150.seconds.ago, "Tap category shoes", type = "userAction", level = null, params = mapOf("category" to "shoes"))
    timeline(149.seconds.ago, "CatalogScreen", type = "navigation", level = null, params = mapOf("category" to "shoes"))
    analytics(149.seconds.ago, "screen_view", "screen" to "catalog", "category" to "shoes")
    api(148.seconds.ago, "GET", "/products?category=shoes&page=1&size=20", 880, response = """{"page":1,"items":[{"id":1007},{"id":1011}]}""")
    api(140.seconds.ago, "GET", "/products?category=shoes&page=2&size=20", 760, response = """{"page":2,"items":[{"id":1020},{"id":1024}]}""")
    databaseQuery(139.seconds.ago, "app_database", "SELECT * FROM products WHERE category = ? ORDER BY price LIMIT ?", "shoes", "20")
    analytics(138.seconds.ago, "scroll_depth", "screen" to "catalog", "percent" to "75")
    timeline(130.seconds.ago, "Image cache is 80% full", type = "cache", level = "warning", params = mapOf("sizeMb" to "164"))

    // product
    timeline(125.seconds.ago, "Tap product 1007", type = "userAction", level = null, params = mapOf("productId" to "1007"))
    timeline(124.seconds.ago, "ProductScreen", type = "navigation", level = null, params = mapOf("productId" to "1007"))
    analytics(124.seconds.ago, "view_item", "item_id" to "1007", "price" to "129.9", "currency" to "EUR")
    api(123.seconds.ago, "GET", "/products/1007", 640, response = """{"id":1007,"name":"Trail Runner 3","price":129.9}""")
    api(123.seconds.ago, "GET", "/products/1007/reviews", 1_480, status = 404, response = """{"error":"not_found"}""")
    api(122.seconds.ago, "GET", "/products/1007/availability?zip=75011", 2_150, response = """{"available":true}""")
    databaseQuery(121.seconds.ago, "app_database", "SELECT * FROM products WHERE id = ?", "1007")
    timeline(120.seconds.ago, "Reviews not found", type = "reviews", level = "debug")

    // cart
    timeline(105.seconds.ago, "Add to cart", type = "userAction", level = null, params = mapOf("productId" to "1007", "size" to "42"))
    analytics(105.seconds.ago, "add_to_cart", "item_id" to "1007", "quantity" to "1", "value" to "129.9")
    api(104.seconds.ago, "POST", "/cart/items", 450, status = 201, request = """{"productId":1007,"quantity":1,"size":42}""", response = """{"cartId":"c-981","items":1,"total":129.9}""")
    databaseQuery(103.seconds.ago, "app_database", "INSERT INTO cart_items (cart_id, product_id, quantity) VALUES (?, ?, ?)", "c-981", "1007", "1")
    timeline(95.seconds.ago, "CartScreen", type = "navigation", level = null)
    api(94.seconds.ago, "GET", "/cart/c-981", 330, response = """{"cartId":"c-981","items":[{"productId":1007,"quantity":1}],"total":129.9}""")

    // checkout: a failure, a timeout, then success
    timeline(80.seconds.ago, "CheckoutScreen", type = "navigation", level = null, params = mapOf("from" to "CartScreen"))
    analytics(80.seconds.ago, "begin_checkout", "value" to "129.9", "items" to "1")
    api(78.seconds.ago, "GET", "/shipping/options?zip=75011", 700, response = """{"options":["standard","express"]}""")
    timeline(70.seconds.ago, "Tap pay", type = "userAction", level = null, params = mapOf("method" to "card"))
    api(69.seconds.ago, "POST", "/checkout", 3_200, status = 500, request = """{"cartId":"c-981","paymentMethod":"card"}""", response = """{"error":"internal_error","traceId":"7f3c2a"}""")
    timeline(66.seconds.ago, "Checkout failed", type = "billing", level = "error", params = mapOf("code" to "500", "traceId" to "7f3c2a"))
    analytics(66.seconds.ago, "checkout_error", "code" to "500")
    network(60.seconds.ago, "POST", "$REST_API/checkout", duration = 8_000.milliseconds, error = "java.net.SocketTimeoutException: timeout")
    timeline(52.seconds.ago, "Checkout timeout, retrying", type = "billing", level = "warning")
    api(48.seconds.ago, "POST", "/checkout", 2_400, request = """{"cartId":"c-981","paymentMethod":"card"}""", response = """{"orderId":"o-5521","status":"confirmed","total":129.9}""")
    databaseQuery(45.seconds.ago, "app_database", "BEGIN", transactionId = "tx-checkout-1")
    databaseQuery(45.seconds.ago, "app_database", "INSERT INTO orders (id, user_id, status, total, currency, payment_method, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)", "o-5521", "42", "confirmed", "129.9", "EUR", "card", "1761700000000", transactionId = "tx-checkout-1")
    databaseQuery(45.seconds.ago, "app_database", "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)", "o-5521", "1007", "1", "129.9", transactionId = "tx-checkout-1")
    databaseQuery(44.seconds.ago, "app_database", "UPDATE products SET stock = stock - ? WHERE id = ?", "1", "1007", transactionId = "tx-checkout-1")
    databaseQuery(44.seconds.ago, "app_database", "COMMIT", transactionId = "tx-checkout-1")
    timeline(44.seconds.ago, "Order confirmed", type = "billing", params = mapOf("orderId" to "o-5521"))
    analytics(44.seconds.ago, "purchase", "transaction_id" to "o-5521", "value" to "129.9", "currency" to "EUR")

    // tracking and sync
    timeline(38.seconds.ago, "OrderTrackingScreen", type = "navigation", level = null, params = mapOf("orderId" to "o-5521"))
    api(37.seconds.ago, "GET", "/orders/o-5521/tracking", 560, response = """{"orderId":"o-5521","status":"packed"}""")
    api(30.seconds.ago, "POST", "/sync/batch", 5_400, request = """{"events":120}""", response = """{"synced":120}""")
    api(30.seconds.ago, "GET", "/recommendations?productId=1007", 1_900, response = """{"items":[{"id":1011}]}""")
    api(24.seconds.ago, "PUT", "/users/42/preferences", 220, status = 204, request = """{"newsletter":false,"theme":"dark"}""", response = "")
    databaseQuery(23.seconds.ago, "app_database", "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC", "42")
    analytics(20.seconds.ago, "screen_view", "screen" to "order_tracking", "order_id" to "o-5521")
    timeline(15.seconds.ago, "Application", type = "lifecycle", level = null, params = mapOf("event" to "onPause"))
    analytics(15.seconds.ago, "app_background")
    timeline(10.seconds.ago, "Application", type = "lifecycle", level = null, params = mapOf("event" to "onResume"))
    api(9.seconds.ago, "GET", "/orders/o-5521/tracking", 900, response = """{"orderId":"o-5521","status":"shipped"}""")
    crash(
        at = 6.seconds.ago,
        exceptionType = "kotlinx.serialization.MissingFieldException",
        message = "Field 'eta' is required for type with serial name 'Tracking', but it was missing",
        stackTrace = """
            kotlinx.serialization.MissingFieldException: Field 'eta' is required for type with serial name 'Tracking', but it was missing
            	at kotlinx.serialization.internal.PluginExceptionsKt.throwMissingFieldException(PluginExceptions.kt:20)
            	at io.github.openflocon.sample.tracking.Tracking.<init>(Tracking.kt:9)
            	at io.github.openflocon.sample.tracking.TrackingRepository.load(TrackingRepository.kt:31)
            	at io.github.openflocon.sample.tracking.TrackingViewModel${'$'}refresh${'$'}1.invokeSuspend(TrackingViewModel.kt:44)
        """.trimIndent(),
    )
}
