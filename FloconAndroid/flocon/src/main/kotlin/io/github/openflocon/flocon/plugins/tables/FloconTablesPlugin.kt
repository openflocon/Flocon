package io.github.openflocon.flocon.plugins.tables

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.tables.model.TableItem
import io.github.openflocon.flocon.plugins.tables.model.tableItemListToJson

internal class FloconTablePluginImpl(
    private val sender: FloconMessageSender,
) : FloconPlugin, FloconTablePlugin {

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
    ) {
        // no op
    }

    override fun onConnectedToServer() {
        // no op
    }

    override fun registerTable(tableItem: TableItem) {
        sendTable(tableItem)
    }

    private fun sendTable(tableItem: TableItem) {
        try {
            sender.send(
                plugin = Protocol.FromDevice.Table.Plugin,
                method = Protocol.FromDevice.Table.Method.AddItems,
                body = tableItemListToJson(listOf(tableItem)).toString() // desktop is expecting an array of table items
            )
        } catch (t: Throwable) {
            FloconLogger.logError("Table json mapping error", t)
        }
    }
}