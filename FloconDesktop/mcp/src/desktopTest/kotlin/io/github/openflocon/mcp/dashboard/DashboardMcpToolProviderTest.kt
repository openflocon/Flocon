package io.github.openflocon.mcp.dashboard

import io.github.openflocon.domain.dashboard.models.DashboardArrangementDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardContainerDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardElementDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardId
import io.github.openflocon.domain.dashboard.models.FormContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.SectionContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.repository.DashboardRepository
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.targetResolver
import io.github.openflocon.mcp.text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DashboardMcpToolProviderTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample"))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
    )
    private val dashboardRepository = FakeDashboardRepository(
        selected = "main",
        dashboards = listOf(
            DashboardDomainModel(
                dashboardId = "main",
                containers = listOf(
                    DashboardContainerDomainModel(
                        name = "Info",
                        containerConfig = SectionContainerConfigDomainModel,
                        elements = listOf(
                            DashboardElementDomainModel.Text(label = "User", value = "Bob", color = null),
                            DashboardElementDomainModel.CheckBox(label = "Debug", value = true, id = "debug"),
                            DashboardElementDomainModel.PlainText(label = "Cfg", value = "{}", type = DashboardElementDomainModel.PlainText.Type.Json),
                        ),
                    ),
                    DashboardContainerDomainModel(
                        name = "Login",
                        containerConfig = FormContainerConfigDomainModel(formId = "login", submitText = "Go"),
                        elements = listOf(
                            DashboardElementDomainModel.TextField(label = "Name", placeHolder = "your name", value = "x", id = "name"),
                            DashboardElementDomainModel.Button(text = "Reset", id = "reset"),
                        ),
                    ),
                ),
            ),
            DashboardDomainModel(dashboardId = "other", containers = emptyList()),
        ),
    )

    private val server = McpTestServer(
        toolProviders = listOf(
            DashboardMcpToolProvider(
                dashboardRepository = dashboardRepository,
                targetResolver = targetResolver(devicesRepository),
            ),
        ),
    )

    @BeforeTest
    fun setUp() = runBlocking { server.start() }

    @AfterTest
    fun tearDown() = runBlocking { server.stop() }

    @Test
    fun `all the tools are read only`() = runBlocking {
        val tools = server.listTools()

        assertEquals(setOf("list_dashboards", "get_dashboard"), tools.map { it.name }.toSet())
        assertTrue(tools.all { it.annotations?.readOnlyHint == true })
    }

    @Test
    fun `list_dashboards returns the ids and the selected one`() = runBlocking {
        val result = server.callTool("list_dashboards").json()

        assertEquals("main", result["selectedDashboardId"]!!.jsonPrimitive.content)
        assertEquals(listOf("main", "other"), result["dashboardIds"]!!.jsonArray.map { it.jsonPrimitive.content })
    }

    @Test
    fun `get_dashboard defaults to the selected dashboard`() = runBlocking {
        val result = server.callTool("get_dashboard").json()

        assertEquals("main", result["dashboardId"]!!.jsonPrimitive.content)
        val containers = result["containers"]!!.jsonArray.map { it.jsonObject }
        assertEquals(listOf("section", "form"), containers.map { it["type"]!!.jsonPrimitive.content })
        assertEquals("login", containers[1]["formId"]!!.jsonPrimitive.content)

        val elements = containers[0]["elements"]!!.jsonArray.map { it.jsonObject }
        assertEquals(listOf("text", "check_box", "json"), elements.map { it["type"]!!.jsonPrimitive.content })
        assertEquals("Bob", elements[0]["value"]!!.jsonPrimitive.content)
        assertEquals(true, elements[1]["checked"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `get_dashboard with an unknown id is an error`() = runBlocking {
        val result = server.callTool("get_dashboard", mapOf("dashboardId" to "nope"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("nope"))
    }
}

private class FakeDashboardRepository(
    private val selected: DashboardId?,
    private val dashboards: List<DashboardDomainModel>,
) : DashboardRepository {

    override fun observeDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboardId: DashboardId): Flow<DashboardDomainModel?> = flowOf(dashboards.firstOrNull { it.dashboardId == dashboardId })

    override fun observeSelectedDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DashboardId?> = flowOf(selected)

    override fun observeDeviceDashboards(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DashboardId>> = flowOf(dashboards.map { it.dashboardId })

    override suspend fun selectDeviceDashboard(dashboardId: DashboardId, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = error("not used")

    override fun observeDashboardArrangement(
        dashboardId: DashboardId,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<DashboardArrangementDomainModel> = error("not used")

    override suspend fun selectDashboardArrangement(
        dashboardId: DashboardId,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        arrangement: DashboardArrangementDomainModel,
    ) = error("not used")

    override suspend fun sendClickEvent(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboardId: DashboardId, buttonId: String) = error("not used")

    override suspend fun submitFormEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dashboardId: DashboardId,
        formId: String,
        values: Map<String, String>,
    ) = error("not used")

    override suspend fun submitTextFieldEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dashboardId: DashboardId,
        textFieldId: String,
        value: String,
    ) = error("not used")

    override suspend fun sendUpdateCheckBoxEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dashboardId: DashboardId,
        checkBoxId: String,
        value: Boolean,
    ) = error("not used")

    override suspend fun deleteDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboardId: DashboardId) = error("not used")
}
