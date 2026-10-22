package io.github.openflocon.flocondesktop.features.network.list

import io.github.openflocon.domain.network.models.NetworkSortDomainModel
import io.github.openflocon.flocondesktop.features.network.list.delegate.HeaderDelegate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NetworkListQueryTest {

    private val header = HeaderDelegate.HeaderQuery(
        sort = NetworkSortDomainModel(NetworkSortDomainModel.Column.Method, asc = true),
        textFilters = emptyList(),
        methods = listOf("GET"),
    )

    private fun build(
        filterText: String = "",
        isClearing: Boolean = false,
        displayOldSessions: Boolean = true,
    ) = buildNetworkListQuery(
        deviceIdAndPackageName = null,
        isClearing = isClearing,
        header = header,
        filterText = filterText,
        displayOldSessions = displayOldSessions,
    )

    @Test
    fun blankFilterTextBecomesNull() {
        assertNull(build(filterText = "   ").filter.filterOnAllColumns)
        assertEquals("abc", build(filterText = "abc").filter.filterOnAllColumns)
    }

    @Test
    fun headerValuesAreForwarded() {
        val query = build()
        assertEquals(header.sort, query.sort)
        assertEquals(header.methods, query.filter.methodFilter)
        assertEquals(header.textFilters, query.filter.textsFilters)
    }

    @Test
    fun settingsAndClearingAreForwarded() {
        assertTrue(build(displayOldSessions = true).filter.displayOldSessions)
        assertFalse(build(displayOldSessions = false).filter.displayOldSessions)
        assertTrue(build(isClearing = true).isClearing)
    }
}
