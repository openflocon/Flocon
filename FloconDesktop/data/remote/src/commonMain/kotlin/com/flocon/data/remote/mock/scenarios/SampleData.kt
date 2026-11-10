package com.flocon.data.remote.mock.scenarios

import com.flocon.data.remote.mock.MockDeviceBuilder
import com.flocon.data.remote.mock.ago
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

// Realistic data of a small shopping app, shared by the bundled scenarios.

internal const val SAMPLE_APP = "Flocon Sample"
internal const val SAMPLE_PACKAGE = "io.github.openflocon.sample"
private const val API = "https://api.flocon-shop.com/v1"
private const val IMAGES = "https://picsum.photos"
private const val JSON = "application/json"

/** An unsigned-looking RS256 JWT expiring in 5 minutes, so the detail shows its claims and expiration. */
private fun sampleJwt(): String {
    val encoder = java.util.Base64.getUrlEncoder().withoutPadding()
    fun part(json: String) = encoder.encodeToString(json.toByteArray())
    val now = System.currentTimeMillis() / 1_000
    return part("""{"alg":"RS256","typ":"JWT"}""") + "." +
        part("""{"sub":"user_42","name":"Jane Doe","role":"admin","iat":${now - 600},"exp":${now + 300}}""") + "." +
        "c2FtcGxlLXNpZ25hdHVyZQ"
}

internal fun MockDeviceBuilder.sampleNetworkCalls() {
    network(12.minutes.ago, "GET", "$API/config", duration = 84.milliseconds) {
        responseHeader("content-type", JSON)
        responseBody("""{"minVersion":"2.1.0","maintenance":false,"features":["checkout_v2","reviews"]}""")
    }
    network(11.minutes.ago, "POST", "$API/auth/login", duration = 312.milliseconds) {
        requestHeader("content-type", JSON)
        requestBody("""{"email":"jane.doe@example.com","password":"••••••••"}""")
        responseHeader("content-type", JSON)
        responseHeader("set-cookie", "session=abc123; HttpOnly")
        responseBody("""{"token":"${sampleJwt()}","expiresIn":3600,"user":{"id":42,"name":"Jane Doe"}}""")
    }
    network(10.minutes.ago, "GET", "$API/users/42", duration = 95.milliseconds) {
        requestHeader("authorization", "Bearer ${sampleJwt()}")
        responseHeader("content-type", JSON)
        responseBody("""{"id":42,"name":"Jane Doe","email":"jane.doe@example.com","premium":true,"addresses":[{"city":"Paris","zip":"75011"}]}""")
    }
    network(9.minutes.ago, "GET", "$API/products?category=shoes&page=1&size=20", duration = 241.milliseconds) {
        responseHeader("content-type", JSON)
        responseHeader("cache-control", "max-age=60")
        responseBody(productsJson(page = 1))
    }
    network(8.minutes.ago, "GET", "$API/products?category=shoes&page=2&size=20", duration = 198.milliseconds) {
        responseHeader("content-type", JSON)
        responseBody(productsJson(page = 2))
    }
    network(7.minutes.ago, "GET", "$API/products/1007", duration = 77.milliseconds) {
        responseHeader("content-type", JSON)
        responseBody("""{"id":1007,"name":"Trail Runner 3","price":129.9,"currency":"EUR","stock":12,"rating":4.6}""")
    }
    network(6.minutes.ago, "GET", "$API/products/1007/reviews", status = 404, duration = 61.milliseconds) {
        responseHeader("content-type", JSON)
        responseBody("""{"error":"not_found","message":"No reviews for this product"}""")
    }
    network(5.minutes.ago, "POST", "$API/cart/items", status = 201, duration = 143.milliseconds) {
        requestHeader("content-type", JSON)
        requestBody("""{"productId":1007,"quantity":1,"size":42}""")
        responseHeader("content-type", JSON)
        responseBody("""{"cartId":"c-981","items":1,"total":129.9}""")
    }
    network(4.minutes.ago, "POST", "https://graphql.flocon-shop.com/graphql", duration = 188.milliseconds) {
        requestHeader("content-type", JSON)
        requestBody("""{"operationName":"Recommendations","query":"query Recommendations { recommendations(limit: 3) { id name } }"}""")
        responseHeader("content-type", JSON)
        responseBody("""{"data":{"recommendations":[{"id":1011,"name":"Daily Sneaker"},{"id":1020,"name":"Rain Boot"},{"id":1033,"name":"Wool Socks"}]}}""")
    }
    network(3.minutes.ago, "POST", "$API/checkout", status = 500, duration = 1_204.milliseconds) {
        requestHeader("content-type", JSON)
        requestBody("""{"cartId":"c-981","paymentMethod":"card"}""")
        responseHeader("content-type", JSON)
        responseBody("""{"error":"internal_error","traceId":"7f3c2a"}""")
    }
    network(2.minutes.ago, "POST", "$API/checkout", duration = 10_000.milliseconds, error = "java.net.SocketTimeoutException: timeout")
    network(90.seconds.ago, "POST", "$API/checkout", duration = 864.milliseconds) {
        requestHeader("content-type", JSON)
        requestBody("""{"cartId":"c-981","paymentMethod":"card"}""")
        responseHeader("content-type", JSON)
        responseBody("""{"orderId":"o-5521","status":"confirmed","total":129.9}""")
    }
    // images: the response is previewed from the url
    network(7.minutes.ago + 20.seconds, "GET", "$IMAGES/id/1025/600/400.jpg", duration = 142.milliseconds) {
        responseHeader("content-type", "image/jpeg")
        responseHeader("cache-control", "public, max-age=31536000, immutable")
    }
    network(7.minutes.ago + 10.seconds, "GET", "$IMAGES/id/237/400/400.jpg", duration = 98.milliseconds) {
        requestHeader("authorization", "Bearer ${sampleJwt()}")
        responseHeader("content-type", "image/jpeg")
    }
    network(6.minutes.ago + 30.seconds, "GET", "$IMAGES/id/1011/800/500.webp", duration = 176.milliseconds) {
        responseHeader("content-type", "image/webp")
    }
    // an image url answering with a json error: the body must stay visible
    network(6.minutes.ago + 20.seconds, "GET", "$API/avatars/missing.png", status = 404, duration = 55.milliseconds) {
        responseHeader("content-type", JSON)
        responseBody("""{"error":"not_found","message":"Avatar missing.png does not exist"}""")
    }
    // many headers on both sides
    network(5.minutes.ago + 30.seconds, "GET", "$API/products/1007/availability?zip=75011", duration = 233.milliseconds) {
        repeat(24) { requestHeader("x-client-trace-$it", "trace-value-$it-" + "a1b2c3".repeat(4)) }
        requestHeader("authorization", "Bearer ${sampleJwt()}")
        requestHeader("accept", "application/json")
        requestHeader("user-agent", "okhttp/4.12.0")
        responseHeader("content-type", JSON)
        repeat(46) { responseHeader("x-edge-metric-$it", "region=eu-west-3;node=$it;cache=${if (it % 3 == 0) "HIT" else "MISS"}") }
        responseHeader("set-cookie", "session=" + "x".repeat(260) + "; Path=/; HttpOnly; Secure")
        responseBody("""{"productId":1007,"available":true,"stores":["Paris 11","Lyon 2","Marseille 1"]}""")
    }
    // very big payloads
    network(5.minutes.ago + 10.seconds, "POST", "$API/catalog/export?format=json", duration = 3_400.milliseconds) {
        requestHeader("content-type", JSON)
        requestBody(bigCatalogJson(2_500))
        responseHeader("content-type", JSON)
        responseHeader("content-length", "${bigCatalogJson(4_000).length}")
        responseBody(bigCatalogJson(4_000))
    }
    network(4.minutes.ago + 40.seconds, "POST", "$API/sync/batch", duration = 12_800.milliseconds) {
        requestHeader("content-type", JSON)
        requestBody(bigCatalogJson(1_500))
        responseHeader("content-type", "text/plain")
        responseBody("OK\n" + "synced item batch line with a long enough content to make the payload heavy\n".repeat(16_000))
    }
    network(60.seconds.ago, "PUT", "$API/users/42/preferences", status = 204, duration = 70.milliseconds) {
        requestHeader("content-type", JSON)
        requestBody("""{"newsletter":false,"theme":"dark"}""")
    }
    network(30.seconds.ago, "DELETE", "$API/cart/c-981", duration = 52.milliseconds)
    grpc(150.seconds.ago, "grpc.example.com", "shop.catalog.CatalogService/ListProducts", duration = 140.milliseconds)
    grpc(100.seconds.ago, "grpc.example.com", "shop.cart.CartService/AddItem", duration = 65.milliseconds)
    grpc(45.seconds.ago, "grpc.example.com", "shop.payment.PaymentService/Charge", grpcStatus = "UNAVAILABLE", duration = 2_100.milliseconds)
    grpc(20.seconds.ago, "grpc.example.com", "shop.orders.OrderService/WatchOrder", pending = true)
    network(10.seconds.ago, "GET", "$API/orders/o-5521/tracking", pending = true)
}

internal fun MockDeviceBuilder.sampleTimeline() {
    timeline(13.minutes.ago, "Application", type = "lifecycle", level = null, params = mapOf("event" to "onCreate"))
    timeline(12.minutes.ago, "SplashScreen", type = "navigation", level = null)
    timeline(11.minutes.ago, "LoginScreen", type = "navigation", level = null, params = mapOf("from" to "SplashScreen"))
    timeline(11.minutes.ago, "Tap login", type = "userAction", level = null, params = mapOf("button" to "login"))
    timeline(10.minutes.ago, "User logged in", type = "auth", params = mapOf("userId" to "42"))
    timeline(9.minutes.ago, "CatalogScreen", type = "navigation", level = null, params = mapOf("category" to "shoes"))
    timeline(8.minutes.ago, "Image cache is 80% full", type = "cache", level = "warning", params = mapOf("sizeMb" to "164"))
    timeline(7.minutes.ago, "ProductScreen", type = "navigation", level = null, params = mapOf("productId" to "1007"))
    timeline(6.minutes.ago, "Reviews not found", type = "reviews", level = "debug")
    timeline(5.minutes.ago, "Add to cart", type = "userAction", level = null, params = mapOf("productId" to "1007", "size" to "42"))
    timeline(4.minutes.ago, "CheckoutScreen", type = "navigation", level = null, params = mapOf("from" to "CartScreen"))
    timeline(3.minutes.ago, "Checkout failed", type = "billing", level = "error", params = mapOf("code" to "500", "traceId" to "7f3c2a"))
    timeline(2.minutes.ago, "Checkout timeout, retrying", type = "billing", level = "warning")
    timeline(90.seconds.ago, "Order confirmed", type = "billing", params = mapOf("orderId" to "o-5521"))
    timeline(60.seconds.ago, "Application", type = "lifecycle", level = null, params = mapOf("event" to "onPause"))
    timeline(45.seconds.ago, "Application", type = "lifecycle", level = null, params = mapOf("event" to "onResume"))
}

internal fun MockDeviceBuilder.sampleCrashes() {
    crash(
        at = 6.minutes.ago,
        exceptionType = "java.lang.IllegalStateException",
        message = "Fragment ProductFragment not attached to a context.",
        stackTrace = """
            java.lang.IllegalStateException: Fragment ProductFragment not attached to a context.
            	at androidx.fragment.app.Fragment.requireContext(Fragment.java:972)
            	at io.github.openflocon.sample.product.ProductFragment.showReviews(ProductFragment.kt:118)
            	at io.github.openflocon.sample.product.ProductFragment.access${'$'}showReviews(ProductFragment.kt:31)
            	at io.github.openflocon.sample.product.ProductFragment${'$'}onViewCreated${'$'}1.invokeSuspend(ProductFragment.kt:64)
            	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
        """.trimIndent(),
    )
    crash(
        at = 3.minutes.ago,
        exceptionType = "kotlinx.serialization.MissingFieldException",
        message = "Field 'orderId' is required for type with serial name 'Order', but it was missing",
        stackTrace = """
            kotlinx.serialization.MissingFieldException: Field 'orderId' is required for type with serial name 'Order', but it was missing
            	at kotlinx.serialization.internal.PluginExceptionsKt.throwMissingFieldException(PluginExceptions.kt:20)
            	at io.github.openflocon.sample.checkout.Order.<init>(Order.kt:12)
            	at io.github.openflocon.sample.checkout.CheckoutRepository.checkout(CheckoutRepository.kt:47)
            	at io.github.openflocon.sample.checkout.CheckoutViewModel${'$'}pay${'$'}1.invokeSuspend(CheckoutViewModel.kt:58)
        """.trimIndent(),
    )
}

internal fun MockDeviceBuilder.sampleAnalytics() {
    analytics(12.minutes.ago, "app_open", "source" to "launcher", "cold_start" to "true")
    analytics(11.minutes.ago, "login", "method" to "email")
    analytics(9.minutes.ago, "screen_view", "screen" to "catalog", "category" to "shoes")
    analytics(8.minutes.ago, "scroll_depth", "screen" to "catalog", "percent" to "75")
    analytics(7.minutes.ago, "screen_view", "screen" to "product", "product_id" to "1007")
    analytics(7.minutes.ago, "view_item", "item_id" to "1007", "price" to "129.9", "currency" to "EUR")
    analytics(5.minutes.ago, "add_to_cart", "item_id" to "1007", "quantity" to "1", "value" to "129.9")
    analytics(4.minutes.ago, "begin_checkout", "value" to "129.9", "items" to "1")
    analytics(3.minutes.ago, "checkout_error", "code" to "500")
    analytics(90.seconds.ago, "purchase", "transaction_id" to "o-5521", "value" to "129.9", "currency" to "EUR")
    analytics(60.seconds.ago, "app_background")
    analytics(10.minutes.ago, "experiment_exposure", "experiment" to "checkout_v2", "variant" to "treatment", tableId = "experiments")
    analytics(9.minutes.ago, "experiment_exposure", "experiment" to "reviews_layout", "variant" to "control", tableId = "experiments")
}

/** Databases only (no tables: the mock ignores the queries sent by the desktop). Long names test the truncation. */
internal fun MockDeviceBuilder.sampleDatabases() {
    val names = listOf(
        "app_database",
        "cache.db",
        "Application Support/BeReal.sqlite",
        "Application Support/Google/FirebaseCore/Measurement/google-app-measurement.sqlite",
        "Documents/very/long/path/to/a/database/shake.sqlite3",
    )
    raw(
        at = 1.minutes.ago,
        plugin = "database",
        method = "get_databases",
        body = names.joinToString(prefix = "[", postfix = "]") { """{"id":"$it","name":"$it"}""" },
    )
}

internal fun MockDeviceBuilder.sampleDeeplinks() {
    deeplinks(at = 10.minutes.ago) {
        deeplink("flocon-shop://home", label = "Home", description = "Opens the home tab")
        deeplink("flocon-shop://product/[productId]", label = "Product", description = "A product page") {
            variable("productId", "productId")
        }
        deeplink("flocon-shop://catalog?category=[category]&sort=[sort]", label = "Catalog", description = "Catalog filtered by category") {
            autoComplete("category", "shoes", "shirts", "jackets", "accessories")
            autoComplete("sort", "price_asc", "price_desc", "newest")
        }
        deeplink("flocon-shop://checkout?cartId=[cartId]&promo=[promo]", label = "Checkout") {
            variable("cartId", "cartId")
            variable("promo", "promoCode")
        }
        deeplink("https://flocon-shop.com/orders/[orderId]", label = "Order tracking (universal link)") {
            variable("orderId", "orderId")
        }
        deeplink("flocon-shop://settings")
        variable("productId", description = "Id of a product, e.g. p-1042")
        variable("cartId", description = "Current cart id")
        variable("promoCode", description = "Promo code to apply", suggestions = listOf("WELCOME10", "SUMMER25", "FREESHIP"))
        variable("orderId", suggestions = listOf("o-5521", "o-5522", "o-5523"))
    }
}

internal fun MockDeviceBuilder.sampleFeatureFlags() {
    featureFlags("firebase", at = 12.minutes.ago) {
        flag("checkout_v2", state = "custom", variant = "treatment", payload = """{"buttonColor":"#FF5722","oneClick":true}""")
        flag("reviews", state = "on", variant = "on")
        flag("dark_mode", state = "on", variant = "on")
        flag("new_onboarding", state = "off", variant = "off")
        flag("search_suggestions", state = "custom", variant = "v2", payload = """{"maxResults":8}""")
    }
    featureFlags("statsig", at = 12.minutes.ago) {
        flag("free_shipping_threshold", state = "custom", variant = "50_eur", payload = "50")
        flag("promo_banner", state = "off", variant = "control")
    }
}

private fun productsJson(page: Int): String {
    val names = listOf("Trail Runner", "Daily Sneaker", "City Loafer", "Rain Boot", "Court Classic")
    val items = (0 until 20).joinToString(",") { index ->
        val id = 1000 + (page - 1) * 20 + index
        """{"id":$id,"name":"${names[index % names.size]} ${index / names.size + 1}","price":${59 + index * 5}.9,"currency":"EUR"}"""
    }
    return """{"page":$page,"size":20,"total":40,"items":[$items]}"""
}

/** A catalog of [count] products: ~200 bytes each, the 4 000 version is a ~800 KB body */
private fun bigCatalogJson(count: Int): String = buildString {
    append("[")
    repeat(count) { index ->
        if (index > 0) append(",")
        append("""{"id":${1000 + index},"sku":"SKU-${index.toString().padStart(6, '0')}","name":"Product $index","price":${(index % 200) + 9.9},"currency":"EUR","stock":${index % 40},"tags":["shoes","outdoor","sale"],"description":"A sample product used to test very large payloads"}""")
    }
    append("]")
}
