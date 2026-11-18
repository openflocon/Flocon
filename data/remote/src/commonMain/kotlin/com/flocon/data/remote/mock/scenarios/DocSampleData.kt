package com.flocon.data.remote.mock.scenarios

import com.flocon.data.remote.mock.MockDeviceBuilder
import com.flocon.data.remote.mock.ago
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

// Realistic data of the same small shopping app for the plugins that need more than a few lines (database, files,
// preferences, images, dashboard, tables, accessibility, webview). Shared by the `doc-*` scenarios and `default`.

private const val APP_FOLDER = "/data/user/0/$SAMPLE_PACKAGE"
private const val IMAGES = "https://images.unsplash.com"
private const val APP_DB = "app_database"
private const val CACHE_DB = "cache.db"

/** `app_database` (Room: users, products, orders, order_items) and `cache.db`, with the queries the app ran. */
internal fun MockDeviceBuilder.sampleDatabases() {
    databases(at = 12.minutes.ago) {
        database(APP_DB) {
            table("users") {
                column("id", "INTEGER", primaryKey = true)
                column("name", "TEXT", nullable = false)
                column("email", "TEXT", nullable = false)
                column("premium", "INTEGER", nullable = false)
                column("city", "TEXT")
                column("created_at", "INTEGER", nullable = false)
                row(42, "Jane Doe", "jane.doe@example.com", 1, "Paris", 1_717_200_000_000)
                row(43, "Marc Dubois", "marc.dubois@example.com", 0, "Lyon", 1_719_800_000_000)
                row(44, "Sofia Rossi", "sofia.rossi@example.com", 1, "Marseille", 1_721_300_000_000)
                row(45, "Liam Smith", "liam.smith@example.com", 0, null, 1_723_900_000_000)
                row(46, "Aiko Tanaka", "aiko.tanaka@example.com", 1, "Nantes", 1_726_400_000_000)
                row(47, "Noah Martin", "noah.martin@example.com", 0, "Lille", 1_728_100_000_000)
                row(48, "Emma Petit", "emma.petit@example.com", 0, "Paris", 1_730_600_000_000)
                row(49, "Lucas Bernard", "lucas.bernard@example.com", 1, "Toulouse", 1_732_100_000_000)
            }
            table("products") {
                column("id", "INTEGER", primaryKey = true)
                column("name", "TEXT", nullable = false)
                column("category", "TEXT", nullable = false)
                column("price", "REAL", nullable = false)
                column("currency", "TEXT", nullable = false)
                column("stock", "INTEGER", nullable = false)
                column("rating", "REAL")
                row(1001, "Trail Runner 1", "shoes", 59.9, "EUR", 34, 4.4)
                row(1002, "Daily Sneaker 1", "shoes", 64.9, "EUR", 58, 4.2)
                row(1003, "City Loafer 1", "shoes", 89.9, "EUR", 12, 4.0)
                row(1004, "Rain Boot 1", "shoes", 79.9, "EUR", 0, 4.7)
                row(1005, "Court Classic 1", "shoes", 99.9, "EUR", 21, null)
                row(1007, "Trail Runner 3", "shoes", 129.9, "EUR", 12, 4.6)
                row(1011, "Wool Socks", "accessories", 14.9, "EUR", 240, 4.8)
                row(1020, "Rain Jacket", "jackets", 149.0, "EUR", 17, 4.3)
                row(1033, "Cotton Shirt", "shirts", 39.0, "EUR", 63, 4.1)
                row(1041, "Leather Belt", "accessories", 29.9, "EUR", 90, 3.9)
            }
            table("orders") {
                column("id", "TEXT", primaryKey = true)
                column("user_id", "INTEGER", nullable = false)
                column("status", "TEXT", nullable = false)
                column("total", "REAL", nullable = false)
                column("currency", "TEXT", nullable = false)
                column("payment_method", "TEXT")
                column("created_at", "INTEGER", nullable = false)
                row("o-5518", 43, "delivered", 64.9, "EUR", "card", 1_761_000_000_000)
                row("o-5519", 44, "shipped", 143.8, "EUR", "paypal", 1_761_300_000_000)
                row("o-5520", 46, "cancelled", 29.9, "EUR", "card", 1_761_500_000_000)
                row("o-5521", 42, "confirmed", 129.9, "EUR", "card", 1_761_700_000_000)
                row("o-5522", 45, "pending", 14.9, "EUR", null, 1_761_800_000_000)
                row("o-5523", 49, "pending", 188.9, "EUR", "card", 1_761_900_000_000)
            }
            table("order_items") {
                column("order_id", "TEXT", nullable = false)
                column("product_id", "INTEGER", nullable = false)
                column("quantity", "INTEGER", nullable = false)
                column("unit_price", "REAL", nullable = false)
                row("o-5518", 1002, 1, 64.9)
                row("o-5519", 1003, 1, 89.9)
                row("o-5519", 1041, 1, 29.9)
                row("o-5519", 1011, 2, 12.0)
                row("o-5520", 1041, 1, 29.9)
                row("o-5521", 1007, 1, 129.9)
                row("o-5522", 1011, 1, 14.9)
                row("o-5523", 1020, 1, 149.0)
                row("o-5523", 1033, 1, 39.0)
            }
        }
        database(CACHE_DB) {
            table("http_cache") {
                column("url", "TEXT", primaryKey = true)
                column("etag", "TEXT")
                column("size_bytes", "INTEGER", nullable = false)
                column("fetched_at", "INTEGER", nullable = false)
                row("https://api.flocon-shop.com/v1/config", "\"a1b2c3\"", 312, 1_761_950_000_000)
                row("https://api.flocon-shop.com/v1/products?category=shoes&page=1&size=20", "\"d4e5f6\"", 4_870, 1_761_950_100_000)
                row("https://api.flocon-shop.com/v1/products/1007", null, 214, 1_761_950_200_000)
                row("https://api.flocon-shop.com/v1/users/42", "\"77aa88\"", 268, 1_761_950_300_000)
            }
        }
    }

    databaseQuery(11.minutes.ago, APP_DB, "SELECT * FROM users WHERE id = ?", "42")
    databaseQuery(9.minutes.ago, APP_DB, "SELECT * FROM products WHERE category = ? ORDER BY price LIMIT ?", "shoes", "20")
    databaseQuery(8.minutes.ago, CACHE_DB, "INSERT OR REPLACE INTO http_cache (url, etag, size_bytes, fetched_at) VALUES (?, ?, ?, ?)", "https://api.flocon-shop.com/v1/products?category=shoes&page=2&size=20", "\"9f8e7d\"", "4912", "1761950400000")
    databaseQuery(7.minutes.ago, APP_DB, "SELECT * FROM products WHERE id = ?", "1007")
    // the checkout runs in a transaction
    databaseQuery(3.minutes.ago + 2.seconds, APP_DB, "BEGIN", transactionId = "tx-checkout-1")
    databaseQuery(3.minutes.ago + 1.seconds, APP_DB, "INSERT INTO orders (id, user_id, status, total, currency, payment_method, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)", "o-5521", "42", "confirmed", "129.9", "EUR", "card", "1761700000000", transactionId = "tx-checkout-1")
    databaseQuery(3.minutes.ago, APP_DB, "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)", "o-5521", "1007", "1", "129.9", transactionId = "tx-checkout-1")
    databaseQuery(3.minutes.ago - 1.seconds, APP_DB, "UPDATE products SET stock = stock - ? WHERE id = ?", "1", "1007", transactionId = "tx-checkout-1")
    databaseQuery(3.minutes.ago - 2.seconds, APP_DB, "COMMIT", transactionId = "tx-checkout-1")
    databaseQuery(90.seconds.ago, APP_DB, "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC", "42")
    databaseQuery(45.seconds.ago, APP_DB, "SELECT COUNT(*) FROM order_items WHERE order_id = ?", "o-5521")
}

/** The `files`, `caches`, `databases` and `shared_prefs` roots, with folders and files. */
internal fun MockDeviceBuilder.sampleFiles() {
    files(at = 12.minutes.ago) {
        root(id = "files", name = "Files", path = "$APP_FOLDER/files") {
            file("config.json", 512, 2.hours.ago, content = """{"apiUrl":"https://api.flocon-shop.com/v1","environment":"staging","analytics":true}""")
            file("session.token", 1_208, 20.minutes.ago, content = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.sample.signature")
            file("last_sync.txt", 24, 5.minutes.ago, content = "2025-10-30T10:42:00Z\n")
            folder("logs", 3.minutes.ago) {
                file("app-2025-10-26.log", 96_256, 4.days.ago)
                file("app-2025-10-27.log", 143_360, 3.days.ago)
                file("app-2025-10-28.log", 184_320, 2.days.ago)
                file("app-2025-10-29.log", 262_144, 1.days.ago)
                file("app-2025-10-30.log", 48_912, 1.minutes.ago, content = "10:41:57 I/Checkout: confirmed o-5521\n10:42:00 D/Sync: done\n")
                file("crash-2025-10-29.txt", 6_144, 1.days.ago, content = "java.lang.IllegalStateException: Fragment ProductFragment not attached to a context.\n")
                file("network.har", 1_480_704, 2.hours.ago)
            }
            folder("images", 40.minutes.ago) {
                file("banner_summer.jpg", 412_672, 3.days.ago)
                file("banner_winter.webp", 188_416, 12.days.ago)
                file("logo.png", 14_336, 30.days.ago)
                file("logo@2x.png", 38_912, 30.days.ago)
                file("placeholder.png", 2_048, 30.days.ago)
                file("product_1007.jpg", 218_112, 2.hours.ago)
                file("product_1011.jpg", 176_128, 2.hours.ago)
                file("product_1020.jpg", 245_760, 5.hours.ago)
                file("promo.gif", 524_288, 8.days.ago)
                file("size_guide.svg", 4_608, 20.days.ago)
            }
            folder("exports", 1.days.ago) {
                file("orders-2025-09.csv", 21_504, 31.days.ago, content = "id,user_id,status,total\no-5300,42,delivered,59.9\n")
                file("orders-2025-10.csv", 18_432, 1.days.ago, content = "id,user_id,status,total\no-5521,42,confirmed,129.9\n")
                file("catalog.json", 802_816, 3.days.ago)
                file("invoice-o-5410.pdf", 96_768, 6.days.ago)
                file("notes.txt", 312, 4.hours.ago, content = "Remember to purge the staging cache before the release.\n")
            }
            folder("databases-backup", 2.days.ago) {
                file("app_database-2025-10-28.db", 98_304, 2.days.ago)
                file("app_database-2025-10-29.db", 102_400, 1.days.ago)
                file("cache-2025-10-29.db", 24_576, 1.days.ago)
            }
            folder("avatars", 6.days.ago) {
                file("user_42.jpg", 86_016, 6.days.ago)
                file("user_43.jpg", 71_680, 6.days.ago)
                file("user_44.png", 120_832, 5.days.ago)
            }
        }
        root(id = "caches", name = "Caches", path = "$APP_FOLDER/cache") {
            folder("image_cache", 4.minutes.ago) {
                file("0a1b2c.0", 412_672, 4.minutes.ago)
                file("3d4e5f.0", 301_056, 6.minutes.ago)
                file("6a7b8c.0", 258_048, 7.minutes.ago)
                file("journal", 1_024, 4.minutes.ago)
            }
            folder("http_cache", 8.minutes.ago) {
                file("entry-1.cache", 4_870, 8.minutes.ago)
                file("entry-2.cache", 214, 7.minutes.ago)
            }
            file("webview_cookies.db", 36_864, 1.hours.ago)
        }
        root(id = "databases", name = "Databases", path = "$APP_FOLDER/databases") {
            file("app_database", 102_400, 3.minutes.ago)
            file("app_database-shm", 32_768, 3.minutes.ago)
            file("app_database-wal", 148_552, 3.minutes.ago)
            file("cache.db", 24_576, 8.minutes.ago)
        }
        root(id = "$APP_FOLDER/shared_prefs", name = "Shared preferences", path = "$APP_FOLDER/shared_prefs") {
            file("io.github.openflocon.sample_preferences.xml", 1_126, 10.minutes.ago)
            file("session.xml", 698, 11.minutes.ago)
        }
    }
}

/** A SharedPreferences file, a session file and a DataStore, with every value type. */
internal fun MockDeviceBuilder.sampleSharedPreferences() {
    sharedPreferences(at = 12.minutes.ago) {
        preferences("io.github.openflocon.sample_preferences", type = "SharedPreferences") {
            string("language", "en")
            string("theme", "dark")
            boolean("notifications_enabled", true)
            boolean("newsletter", false)
            int("launch_count", 27)
            long("last_sync_timestamp", 1_761_950_400_000)
            float("font_scale", 1.15f)
            stringSet("favorite_categories", "shoes", "jackets", "accessories")
            string("selected_store", "Paris 11")
        }
        preferences("session", type = "SharedPreferences") {
            string("token", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyXzQyIn0.c2FtcGxl")
            long("userId", 42)
            string("userName", "Jane Doe")
            boolean("isLogged", true)
            boolean("isPremium", true)
            long("expiresAt", 1_761_954_000_000)
        }
        preferences("shop_datastore", type = "DataStore") {
            string("cart_id", "c-981")
            int("cart_items_count", 1)
            float("cart_total", 129.9f)
            string("last_search", "trail runner")
            boolean("onboarding_done", true)
            stringSet("recently_viewed", "1007", "1011", "1020")
        }
    }
}

/** Image downloads (product photos): the desktop draws them from their url. */
internal fun MockDeviceBuilder.sampleImages() {
    // photo id, width, height, format
    val photos = listOf(
        listOf("1460353581641-37baddab0fa2", 600, 400, "jpg"),
        listOf("1525966222134-fcfa99b8ae77", 400, 400, "jpg"),
        listOf("1600185365926-3a2ce3cdb9eb", 800, 500, "webp"),
        listOf("1595950653106-6c9ebd614d3a", 800, 600, "jpg"),
        listOf("1514989940723-e8e51635b782", 500, 500, "png"),
        listOf("1606107557195-0e29a4b5b4aa", 640, 480, "jpg"),
        listOf("1560769629-975ec94e6a86", 300, 300, "jpg"),
        listOf("1551107696-a4b0c5a0d9a2", 600, 800, "jpg"),
        listOf("1543508282-6319a3e2621f", 900, 600, "webp"),
        listOf("1539185441755-769473a23570", 400, 300, "jpg"),
        listOf("1556906781-9a412961c28c", 500, 700, "jpg"),
        listOf("1608231387042-66d1773070a5", 800, 450, "png"),
    )
    photos.forEachIndexed { index, (id, width, height, format) ->
        network((10.minutes - (index * 40).seconds).ago, "GET", "$IMAGES/photo-$id?w=$width&h=$height&fit=crop&fm=$format", duration = (80 + index * 17).milliseconds) {
            requestHeader("accept", "image/avif,image/webp,image/*")
            requestHeader("user-agent", "okhttp/4.12.0")
            responseHeader("content-type", "image/${if (format == "jpg") "jpeg" else format}")
            responseHeader("cache-control", "public, max-age=31536000, immutable")
            responseSize(90_000L + index * 21_000L)
            responseHeader("content-length", (90_000 + index * 21_000).toString())
        }
    }
}

/** Two dashboards: the signed-in user (sections and a form) and a debug one (check boxes, json, markdown, html). */
internal fun MockDeviceBuilder.sampleDashboard() {
    val green = 0xFF2E7D32.toInt()
    val orange = 0xFFEF6C00.toInt()
    val red = 0xFFC62828.toInt()
    dashboard("user", at = 12.minutes.ago) {
        section("Account") {
            text("User id", "42")
            text("Name", "Jane Doe")
            text("Email", "jane.doe@example.com")
            label("Premium member", color = green)
            text("Member since", "June 2024", color = orange)
            button("user_logout", "Log out")
            button("user_refresh_token", "Refresh token")
        }
        section("Session") {
            json("Token claims", """{"sub":"user_42","role":"admin","iat":1761950400,"exp":1761954000}""")
            label("Token expires in 5 minutes", color = orange)
            checkBox("user_remember_me", "Remember me", value = true)
        }
        form("Edit profile", formId = "user_edit", submitText = "Save") {
            textField("user_name", "Name", value = "Jane Doe", placeHolder = "Your name")
            textField("user_city", "City", value = "Paris", placeHolder = "Your city")
            checkBox("user_newsletter", "Subscribe to the newsletter", value = false)
        }
    }
    dashboard("debug", at = 12.minutes.ago) {
        section("Environment") {
            text("API", "https://api.flocon-shop.com/v1")
            text("Build", "2.3.0 (230)")
            text("Variant", "staging")
            label("Connected to staging", color = red)
            textField("debug_api_url", "API url", value = "https://api.flocon-shop.com/v1", placeHolder = "https://...")
        }
        section("Toggles") {
            checkBox("debug_force_checkout_v2", "Force checkout v2", value = true)
            checkBox("debug_mock_payments", "Mock payments", value = false)
            checkBox("debug_show_ids", "Show product ids", value = true)
            button("debug_clear_cache", "Clear image cache")
            button("debug_crash", "Trigger a test crash")
        }
        section("Remote config") {
            json("Last fetched", """{"minVersion":"2.1.0","maintenance":false,"features":["checkout_v2","reviews"],"freeShippingThreshold":50}""")
            markdown("Release notes", "## 2.3.0\n- **Checkout v2** with one-click payment\n- Faster catalog loading\n- Fix: reviews not showing on small screens")
            html("Terms banner", "<p>By ordering you accept our <b>terms of sale</b>.</p>")
        }
    }
}

/** Two tables filled by the app: orders and search history. */
internal fun MockDeviceBuilder.sampleTables() {
    val orders = listOf(
        listOf("o-5518", "Marc Dubois", "1", "64.9", "delivered"),
        listOf("o-5519", "Sofia Rossi", "4", "143.8", "shipped"),
        listOf("o-5520", "Aiko Tanaka", "1", "29.9", "cancelled"),
        listOf("o-5521", "Jane Doe", "1", "129.9", "confirmed"),
        listOf("o-5522", "Liam Smith", "1", "14.9", "pending"),
        listOf("o-5523", "Lucas Bernard", "2", "188.9", "pending"),
    )
    orders.forEachIndexed { index, (id, customer, items, total, status) ->
        tableRow((12 - index * 2).minutes.ago, "orders", "id" to id, "customer" to customer, "items" to items, "total" to total, "status" to status)
    }
    val searches = listOf(
        Triple("running shoes", 24, "catalog"),
        Triple("trail runner", 6, "catalog"),
        Triple("wool socks", 3, "catalog"),
        Triple("rain jacket", 5, "suggestions"),
        Triple("belt", 2, "suggestions"),
        Triple("sneaker", 18, "catalog"),
    )
    searches.forEachIndexed { index, (query, results, source) ->
        tableRow((11 - index * 2).minutes.ago + 30.seconds, "search_history", "query" to query, "results" to results.toString(), "source" to source)
    }
}

/** TalkBack on, then focus moves, clicks, an announcement and a button without label. */
internal fun MockDeviceBuilder.sampleAccessibility() {
    val activity = "$SAMPLE_PACKAGE.product.ProductActivity"
    accessibilityState(at = 12.minutes.ago)
    accessibility(
        at = 9.minutes.ago,
        type = "WINDOW_STATE_CHANGED",
        text = "Trail Runner 3",
        className = activity,
        screen = activity,
        extras = mapOf("packageName" to SAMPLE_PACKAGE),
    )
    accessibility(
        at = 8.minutes.ago,
        type = "ELEMENT_FOCUSED",
        text = "Trail Runner 3",
        contentDescription = "Trail Runner 3, heading",
        className = "android.widget.TextView",
        screen = activity,
        bounds = "[48,240][1032,360]",
        extras = mapOf("spoken" to "Trail Runner 3, Heading", "role" to "Heading", "heading" to "true"),
    )
    accessibility(
        at = 7.minutes.ago + 30.seconds,
        type = "ELEMENT_FOCUSED",
        text = "129,90 €",
        contentDescription = "Price: 129 euros 90",
        className = "android.widget.TextView",
        screen = activity,
        bounds = "[48,380][400,460]",
        extras = mapOf("spoken" to "Price: 129 euros 90"),
    )
    accessibility(
        at = 7.minutes.ago,
        type = "ELEMENT_FOCUSED",
        text = "Size 42",
        contentDescription = "Size 42",
        className = "android.widget.CheckBox",
        screen = activity,
        bounds = "[48,520][1032,640]",
        extras = mapOf(
            "spoken" to "Size 42, Check box, not checked, double tap to toggle",
            "role" to "Check box",
            "stateDescription" to "not checked",
            "clickable" to "true",
            "enabled" to "true",
        ),
    )
    accessibility(
        at = 6.minutes.ago + 30.seconds,
        type = "ELEMENT_FOCUSED",
        text = "Add to cart",
        contentDescription = "Add to cart",
        className = "android.widget.Button",
        screen = activity,
        bounds = "[48,1530][1032,1674]",
        extras = mapOf(
            "spoken" to "Add to cart, Button, double tap to activate",
            "role" to "Button",
            "clickable" to "true",
            "enabled" to "true",
            "customActions" to "Add to wishlist | Share",
        ),
    )
    accessibility(
        at = 6.minutes.ago,
        type = "CLICK",
        text = "Add to cart",
        contentDescription = "Add to cart",
        className = "Button",
        screen = activity,
        bounds = "[48,1530][1032,1674]",
        extras = mapOf("source" to "compose", "testTag" to "add_to_cart", "enabled" to "true"),
    )
    accessibility(
        at = 5.minutes.ago + 55.seconds,
        type = "ANNOUNCEMENT",
        text = "Trail Runner 3 added to your cart",
        screen = activity,
        extras = mapOf("packageName" to SAMPLE_PACKAGE),
    )
    accessibility(
        at = 5.minutes.ago,
        type = "WINDOW_STATE_CHANGED",
        text = "Cart",
        className = "$SAMPLE_PACKAGE.cart.CartActivity",
        screen = "$SAMPLE_PACKAGE.cart.CartActivity",
        extras = mapOf("packageName" to SAMPLE_PACKAGE),
    )
    accessibility(
        at = 4.minutes.ago + 30.seconds,
        type = "ELEMENT_FOCUSED",
        text = "Remove Trail Runner 3",
        contentDescription = "Remove Trail Runner 3",
        className = "android.widget.ImageButton",
        screen = "$SAMPLE_PACKAGE.cart.CartActivity",
        bounds = "[936,420][1032,516]",
        extras = mapOf("spoken" to "Remove Trail Runner 3, Button, double tap to activate", "role" to "Button", "clickable" to "true"),
    )
    // an icon button nobody labeled: what the audit is there for
    accessibility(
        at = 4.minutes.ago,
        type = "CLICK",
        className = "android.widget.ImageButton",
        screen = "$SAMPLE_PACKAGE.cart.CartActivity",
        bounds = "[900,96][1032,228]",
        extras = mapOf("source" to "view", "viewId" to "cart_more_options", "enabled" to "true", "missingLabel" to "true"),
    )
    accessibility(
        at = 3.minutes.ago + 30.seconds,
        type = "ELEMENT_FOCUSED",
        text = "Checkout",
        contentDescription = "Checkout, 129 euros 90",
        className = "android.widget.Button",
        screen = "$SAMPLE_PACKAGE.cart.CartActivity",
        bounds = "[48,1530][1032,1674]",
        extras = mapOf("spoken" to "Checkout, 129 euros 90, Button, double tap to activate", "role" to "Button", "clickable" to "true", "enabled" to "true"),
    )
    accessibility(
        at = 3.minutes.ago,
        type = "ANNOUNCEMENT",
        text = "Payment failed, please try again",
        screen = "$SAMPLE_PACKAGE.checkout.CheckoutActivity",
    )
}

/** The checkout page of a WebView: console logs, page events and calls to the native bridge. */
internal fun MockDeviceBuilder.sampleWebView() {
    val webView = "checkout"
    val page = "https://pay.flocon-shop.com/checkout?cart=c-981"
    val script = "https://pay.flocon-shop.com/static/checkout.js"
    webViewPage(5.minutes.ago, webView, "page_started", url = page)
    webViewConsole(5.minutes.ago - 100.milliseconds, webView, "log", "Checkout page loaded", url = page, sourceId = script, lineNumber = 12)
    webViewPage(5.minutes.ago - 400.milliseconds, webView, "page_finished", url = page)
    webViewBridge(4.minutes.ago + 50.seconds, webView, "Android", "getAuthToken", result = "eyJhbGciOiJSUzI1NiJ9.sample", url = page)
    webViewConsole(4.minutes.ago + 48.seconds, webView, "info", "Payment methods loaded: card, paypal, apple_pay", url = page, sourceId = script, lineNumber = 88)
    webViewBridge(4.minutes.ago + 40.seconds, webView, "Android", "getCart", result = """{"cartId":"c-981","items":1,"total":129.9}""", url = page)
    webViewConsole(4.minutes.ago + 30.seconds, webView, "debug", "Validating card number", url = page, sourceId = script, lineNumber = 131)
    webViewConsole(4.minutes.ago + 10.seconds, webView, "warn", "Deprecated API: document.write() is being called", url = page, sourceId = script, lineNumber = 204)
    webViewBridge(4.minutes.ago, webView, "Android", "trackEvent", arguments = """["begin_payment",{"cartId":"c-981"}]""", url = page)
    webViewConsole(3.minutes.ago + 20.seconds, webView, "error", "Uncaught TypeError: Cannot read properties of undefined (reading 'token')", url = page, sourceId = script, lineNumber = 342)
    webViewPage(3.minutes.ago + 15.seconds, webView, "http_error", url = "https://pay.flocon-shop.com/api/pay", message = "Internal Server Error", statusCode = 500)
    webViewBridge(3.minutes.ago + 10.seconds, webView, "Android", "showToast", arguments = """["Payment failed"]""", url = page)
    webViewPage(2.minutes.ago, webView, "navigation", url = "https://pay.flocon-shop.com/checkout/retry")
    webViewPage(2.minutes.ago - 300.milliseconds, webView, "ssl_error", url = "https://cdn.flocon-shop.com/fonts/inter.woff2", message = "The certificate is not trusted")
    webViewBridge(90.seconds.ago, webView, "Android", "closeWebView", url = page)
    // a second WebView: the help center
    val help = "https://help.flocon-shop.com/returns"
    webViewPage(1.minutes.ago, "help", "page_started", url = help)
    webViewConsole(1.minutes.ago - 200.milliseconds, "help", "log", "Help center ready", url = help, sourceId = "https://help.flocon-shop.com/app.js", lineNumber = 7)
    webViewPage(1.minutes.ago - 600.milliseconds, "help", "page_finished", url = help)
}
