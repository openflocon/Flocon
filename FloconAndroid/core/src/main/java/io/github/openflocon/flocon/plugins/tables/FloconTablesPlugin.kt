package io.github.openflocon.flocon.plugins.tables

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.tables.model.TableItem
import io.github.openflocon.flocon.plugins.tables.model.tableItemListToJson
import org.json.JSONArray
import java.util.concurrent.ConcurrentLinkedQueue

class FloconTablePluginImpl(
    private val sender: FloconMessageSender,
) : FloconTablePlugin {

    private val tableMessages = ConcurrentLinkedQueue<TableItem>()

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
        sender: FloconMessageSender,
    ) {
        when (messageFromServer.method) {
            Protocol.ToDevice.Table.Method.ClearItems -> {
                val items = readIds(messageFromServer.body)
                if (items.isNotEmpty()) {
                    clearItems(items.toSet())
                }
            }
        }
    }

    private fun readIds(body: String): List<String> {
        return try {
            val array = JSONArray(body)
            val items = mutableListOf<String>()
            for (i in 0 until array.length()) {
                items.add(array.getString(i))
            }
            items
        } catch (t: Throwable) {
            t.printStackTrace()
            emptyList()
        }
    }

    override fun onConnectedToServer(sender: FloconMessageSender) {
        sendTables()
    }

    override fun registerTable(tableItem: TableItem) {
        tableMessages.add(tableItem)
        sendTables()
    }

    private fun sendTables() {
        tableMessages.takeIf { it.isNotEmpty() }?.let { toSend ->
            sender.send(
                plugin = Protocol.FromDevice.Table.Plugin,
                method = Protocol.FromDevice.Table.Method.AddItems,
                body = tableItemListToJson(tableMessages).toString()
            )
        }
    }

    private fun clearItems(ids: Set<String>) {
        val iterator = tableMessages.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.id in ids) {
                iterator.remove()
            }
        }
    }
}