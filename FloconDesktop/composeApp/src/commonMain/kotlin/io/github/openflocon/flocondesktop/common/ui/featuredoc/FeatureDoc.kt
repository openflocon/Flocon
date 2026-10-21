package io.github.openflocon.flocondesktop.common.ui.featuredoc

import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.feature_doc_analytics_empty_description
import flocondesktop.composeapp.generated.resources.feature_doc_analytics_empty_title
import flocondesktop.composeapp.generated.resources.feature_doc_crashreporter_empty_description
import flocondesktop.composeapp.generated.resources.feature_doc_crashreporter_empty_title
import flocondesktop.composeapp.generated.resources.feature_doc_dashboard_empty_description
import flocondesktop.composeapp.generated.resources.feature_doc_dashboard_empty_title
import flocondesktop.composeapp.generated.resources.feature_doc_database_empty_description
import flocondesktop.composeapp.generated.resources.feature_doc_database_empty_title
import flocondesktop.composeapp.generated.resources.feature_doc_deeplinks_empty_description
import flocondesktop.composeapp.generated.resources.feature_doc_deeplinks_empty_title
import flocondesktop.composeapp.generated.resources.feature_doc_images_empty_description
import flocondesktop.composeapp.generated.resources.feature_doc_images_empty_title
import flocondesktop.composeapp.generated.resources.feature_doc_network_empty_description
import flocondesktop.composeapp.generated.resources.feature_doc_network_empty_title
import flocondesktop.composeapp.generated.resources.feature_doc_sharedpreferences_empty_description
import flocondesktop.composeapp.generated.resources.feature_doc_sharedpreferences_empty_title
import flocondesktop.composeapp.generated.resources.feature_doc_tables_empty_description
import flocondesktop.composeapp.generated.resources.feature_doc_tables_empty_title
import org.jetbrains.compose.resources.StringResource

private const val DOC_BASE_URL = "https://openflocon.github.io/Flocon/"

/**
 * Links every desktop page to its dedicated page of the documentation
 * (see `docs/` at the root of the repository).
 *
 * @param page the mkdocs page, without extension (`docs/network.md` -> `network`)
 * @param setupAnchor the heading explaining how to set the feature up, used by the empty state
 */
enum class FeatureDoc(
    private val page: String,
    private val setupAnchor: String?,
    val emptyTitle: StringResource?,
    val emptyDescription: StringResource?,
) {
    Network(
        page = "network",
        setupAnchor = "setup-integration",
        emptyTitle = Res.string.feature_doc_network_empty_title,
        emptyDescription = Res.string.feature_doc_network_empty_description,
    ),
    Images(
        page = "image",
        setupAnchor = "setup-with-coil-3",
        emptyTitle = Res.string.feature_doc_images_empty_title,
        emptyDescription = Res.string.feature_doc_images_empty_description,
    ),
    Database(
        page = "database",
        setupAnchor = "setup",
        emptyTitle = Res.string.feature_doc_database_empty_title,
        emptyDescription = Res.string.feature_doc_database_empty_description,
    ),
    SharedPreferences(
        page = "sharedpref",
        setupAnchor = "setup-registration",
        emptyTitle = Res.string.feature_doc_sharedpreferences_empty_title,
        emptyDescription = Res.string.feature_doc_sharedpreferences_empty_description,
    ),
    // The root always lists the cache & files folders, an empty folder is a legit state: no empty state
    Files(
        page = "files",
        setupAnchor = "setup",
        emptyTitle = null,
        emptyDescription = null,
    ),
    Dashboard(
        page = "dashboard",
        setupAnchor = "setup",
        emptyTitle = Res.string.feature_doc_dashboard_empty_title,
        emptyDescription = Res.string.feature_doc_dashboard_empty_description,
    ),
    Analytics(
        page = "analytics",
        setupAnchor = "setup",
        emptyTitle = Res.string.feature_doc_analytics_empty_title,
        emptyDescription = Res.string.feature_doc_analytics_empty_description,
    ),
    Tables(
        page = "table",
        setupAnchor = "setup",
        emptyTitle = Res.string.feature_doc_tables_empty_title,
        emptyDescription = Res.string.feature_doc_tables_empty_description,
    ),
    Deeplinks(
        page = "deeplink",
        setupAnchor = "defining-deeplinks-in-kotlin",
        emptyTitle = Res.string.feature_doc_deeplinks_empty_title,
        emptyDescription = Res.string.feature_doc_deeplinks_empty_description,
    ),
    CrashReporter(
        page = "crashreporter",
        setupAnchor = "setup",
        emptyTitle = Res.string.feature_doc_crashreporter_empty_title,
        emptyDescription = Res.string.feature_doc_crashreporter_empty_description,
    ),

    // Desktop only tool: always usable, so it has no empty state, only a documentation link
    AdbCommander(
        page = "adb-commander",
        setupAnchor = null,
        emptyTitle = null,
        emptyDescription = null,
    );

    /** Root of the documentation page, opened by the (i) button. */
    val url: String
        get() = "$DOC_BASE_URL$page/"

    /** Setup section of the documentation page, opened from the empty state. */
    val setupUrl: String
        get() = setupAnchor?.let { "$url#$it" } ?: url
}
