package io.github.openflocon.flocondesktop.features.proxy.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.proxy_activate
import flocondesktop.composeapp.generated.resources.proxy_android_step_app
import flocondesktop.composeapp.generated.resources.proxy_android_step_download
import flocondesktop.composeapp.generated.resources.proxy_android_step_install
import flocondesktop.composeapp.generated.resources.proxy_android_wifi_1
import flocondesktop.composeapp.generated.resources.proxy_android_wifi_2
import flocondesktop.composeapp.generated.resources.proxy_app_heading
import flocondesktop.composeapp.generated.resources.proxy_back
import flocondesktop.composeapp.generated.resources.proxy_binary_not_found
import flocondesktop.composeapp.generated.resources.proxy_certificate_heading
import flocondesktop.composeapp.generated.resources.proxy_copy
import flocondesktop.composeapp.generated.resources.proxy_devices_hint
import flocondesktop.composeapp.generated.resources.proxy_disable
import flocondesktop.composeapp.generated.resources.proxy_done
import flocondesktop.composeapp.generated.resources.proxy_experimental
import flocondesktop.composeapp.generated.resources.proxy_failed
import flocondesktop.composeapp.generated.resources.proxy_intro
import flocondesktop.composeapp.generated.resources.proxy_ios_step_app
import flocondesktop.composeapp.generated.resources.proxy_ios_step_download
import flocondesktop.composeapp.generated.resources.proxy_ios_step_install
import flocondesktop.composeapp.generated.resources.proxy_ios_step_trust
import flocondesktop.composeapp.generated.resources.proxy_ios_wifi_1
import flocondesktop.composeapp.generated.resources.proxy_ios_wifi_2
import flocondesktop.composeapp.generated.resources.proxy_next
import flocondesktop.composeapp.generated.resources.proxy_no_address
import flocondesktop.composeapp.generated.resources.proxy_off_title
import flocondesktop.composeapp.generated.resources.proxy_once_hint
import flocondesktop.composeapp.generated.resources.proxy_persist_hint
import flocondesktop.composeapp.generated.resources.proxy_pick_network
import flocondesktop.composeapp.generated.resources.proxy_platform_android
import flocondesktop.composeapp.generated.resources.proxy_platform_ios
import flocondesktop.composeapp.generated.resources.proxy_port
import flocondesktop.composeapp.generated.resources.proxy_running
import flocondesktop.composeapp.generated.resources.proxy_server
import flocondesktop.composeapp.generated.resources.proxy_starting
import flocondesktop.composeapp.generated.resources.proxy_step_app_label
import flocondesktop.composeapp.generated.resources.proxy_step_certificate_label
import flocondesktop.composeapp.generated.resources.proxy_step_wifi_label
import flocondesktop.composeapp.generated.resources.proxy_title
import flocondesktop.composeapp.generated.resources.proxy_wifi_3
import flocondesktop.composeapp.generated.resources.proxy_wifi_heading
import flocondesktop.composeapp.generated.resources.proxy_wifi_manual
import flocondesktop.composeapp.generated.resources.proxy_wifi_proxy_label
import io.github.openflocon.flocondesktop.features.connect.view.QrCodeImage
import io.github.openflocon.flocondesktop.features.proxy.ProxyViewModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyAddressUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyMoodUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyStatusUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyUiState
import io.github.openflocon.flocondesktop.features.proxy.model.previewProxyUiState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconCircularProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconMascotDialog
import io.github.openflocon.library.designsystem.components.FloconOutlinedButton
import io.github.openflocon.library.designsystem.components.FloconPixelPalette
import io.github.openflocon.library.designsystem.components.FloconSegmentedTextToggle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private val DialogWidth = 720.dp
private val ContentHeight = 260.dp
private val WifiCardWidth = 260.dp
private val SettingsRowHeight = 44.dp
private val QrSize = 176.dp
private val LabelWidth = 56.dp
private val StepNumberSize = 22.dp
private val StepCheckSize = 14.dp
private val StepperLineWidth = 24.dp
private val StatusDotSize = 8.dp
private val ActivateButtonHeight = 44.dp
private val IntroMaxWidth = 440.dp
private const val DisabledAlpha = 0.4f

// code, not translated
private const val USER_CERTIFICATES_SNIPPET = """<debug-overrides>
  <trust-anchors>
    <certificates src="user" />
  </trust-anchors>
</debug-overrides>"""

/** Starts / stops the proxy and walks the user through the setup of the phone. */
@Composable
fun ProxyDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ProxyViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProxyDialog(
        uiState = uiState,
        onEnabledChange = viewModel::onEnabledChange,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    )
}

private enum class SetupStep { Wifi, Certificate, App }

private enum class PhonePlatform { Android, Ios }

@Composable
private fun ProxyDialog(
    uiState: ProxyUiState,
    onEnabledChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var step by remember { mutableStateOf(SetupStep.Wifi) }
    var platform by remember { mutableStateOf(PhonePlatform.Android) }
    val addresses = (uiState.status as? ProxyStatusUiModel.Running)?.addresses
    // the steps need the address of the running proxy
    val canSetUp = !addresses.isNullOrEmpty()
    // turned off, the setup starts over from the first step
    LaunchedEffect(canSetUp) {
        if (!canSetUp) step = SetupStep.Wifi
    }

    FloconMascotDialog(
        onDismissRequest = onDismissRequest,
        width = DialogWidth,
        mascotEnabled = uiState.mascotEnabled,
        mascotPawsY = ProxyMascotPeekPawsY,
        mascot = { ProxyMascotPeek(mood = uiState.mood, modifier = it) },
        modifier = modifier,
    ) {
        Column {
            Header(
                mood = uiState.mood,
                codeRain = uiState.mascotEnabled,
                running = canSetUp,
                onClose = onDismissRequest,
            )
            Box(Modifier.fillMaxWidth().height(1.dp).background(FloconTheme.colors.line))
            Column(
                modifier = Modifier.padding(FloconTheme.spacing.extraLarge),
                verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
            ) {
                Stepper(
                    current = step,
                    platform = platform,
                    enabled = canSetUp,
                    onStepSelected = { step = it },
                    onPlatformSelected = { platform = it },
                )
                // the same height whatever the state, so that the dialog does not jump when the proxy starts
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ContentHeight)
                        .background(FloconTheme.colors.bg100, FloconTheme.shapes.large)
                        .border(1.dp, FloconTheme.colors.line, FloconTheme.shapes.large)
                        .padding(FloconTheme.spacing.extraLarge),
                    contentAlignment = Alignment.Center,
                ) {
                    when (val status = uiState.status) {
                        ProxyStatusUiModel.Off -> OffPanel(onActivate = { onEnabledChange(true) })

                        ProxyStatusUiModel.Starting -> Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
                        ) {
                            FloconCircularProgressIndicator(modifier = Modifier.size(16.dp))
                            Message(text = stringResource(Res.string.proxy_starting))
                        }

                        ProxyStatusUiModel.BinaryNotFound -> Message(
                            text = stringResource(Res.string.proxy_binary_not_found),
                            color = FloconTheme.colors.statusWarning,
                        )

                        is ProxyStatusUiModel.Failed -> Message(
                            text = stringResource(Res.string.proxy_failed, status.message),
                            color = FloconTheme.colors.statusError,
                        )

                        is ProxyStatusUiModel.Running -> if (status.addresses.isEmpty()) {
                            Message(text = stringResource(Res.string.proxy_no_address))
                        } else {
                            StepContent(step = step, platform = platform, addresses = status.addresses)
                        }
                    }
                }
                StepText(
                    text = stringResource(
                        when {
                            !canSetUp -> Res.string.proxy_persist_hint
                            step == SetupStep.App -> Res.string.proxy_devices_hint
                            else -> Res.string.proxy_once_hint
                        },
                    ),
                    // two lines whatever the text, so that the dialog keeps its height
                    minLines = 2,
                )
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(FloconTheme.colors.line))
            Footer(
                enabled = uiState.enabled,
                canNavigate = canSetUp,
                step = step,
                onDisable = { onEnabledChange(false) },
                onBack = { step = SetupStep.entries[step.ordinal - 1] },
                onNext = { step = SetupStep.entries[step.ordinal + 1] },
                onDone = onDismissRequest,
            )
        }
    }
}

@Composable
private fun Header(
    mood: ProxyMoodUiModel,
    codeRain: Boolean,
    running: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val base = FloconTheme.colors.bg000
    // a hint of the rain's color in the bar, solid behind the title and the badge so they stay readable
    val barColor = when {
        !codeRain -> base
        mood == ProxyMoodUiModel.Watching -> FloconPixelPalette.Neon.copy(alpha = 0.04f).compositeOver(base)
        mood == ProxyMoodUiModel.Glitched -> FloconPixelPalette.Red.copy(alpha = 0.06f).compositeOver(base)
        else -> base
    }
    Box(modifier = modifier.fillMaxWidth().background(barColor)) {
        // the code rains while the proxy runs (red on a failure); asleep, a cursor types "Wake up…"
        if (codeRain && mood != ProxyMoodUiModel.Asleep) {
            ProxyCodeRain(
                color = if (mood == ProxyMoodUiModel.Glitched) FloconPixelPalette.Red else FloconPixelPalette.Neon,
                headColor = if (mood == ProxyMoodUiModel.Watching) FloconPixelPalette.NeonHead else null,
                animated = true,
                modifier = Modifier.matchParentSize(),
            )
        }
        HeaderContent(
            labelBackground = barColor.copy(alpha = 0.85f),
            wakeUp = codeRain && mood == ProxyMoodUiModel.Asleep,
            running = running,
            onClose = onClose,
        )
    }
}

@Composable
private fun HeaderContent(
    labelBackground: Color,
    wakeUp: Boolean,
    running: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = FloconTheme.spacing.large, end = FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        Text(
            text = stringResource(Res.string.proxy_title),
            style = FloconTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = FloconTheme.colors.ink,
            modifier = Modifier
                .background(labelBackground, FloconTheme.shapes.small)
                .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.tiny),
        )
        Text(
            text = stringResource(Res.string.proxy_experimental).uppercase(),
            style = FloconTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = FloconTheme.colors.accent,
            modifier = Modifier
                .background(FloconTheme.colors.accentSoft.compositeOver(labelBackground.copy(alpha = 1f)), FloconTheme.shapes.small)
                .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.tiny),
        )
        if (wakeUp) {
            ProxyWakeUpText(modifier = Modifier.padding(start = FloconTheme.spacing.small))
        }
        Box(Modifier.weight(1f))
        if (running) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                modifier = Modifier
                    .background(labelBackground, FloconTheme.shapes.small)
                    .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.tiny),
            ) {
                Box(Modifier.size(StatusDotSize).background(FloconTheme.colors.statusSuccess, CircleShape))
                Text(
                    text = stringResource(Res.string.proxy_running),
                    style = FloconTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FloconTheme.colors.statusSuccess,
                )
            }
        }
        FloconIconButton(onClick = onClose, modifier = Modifier.padding(vertical = FloconTheme.spacing.small)) {
            FloconIcon(Icons.Outlined.Close)
        }
    }
}

/** The 3 steps of the setup of a phone, and the platform they are written for. */
@Composable
private fun Stepper(
    current: SetupStep,
    platform: PhonePlatform,
    enabled: Boolean,
    onStepSelected: (SetupStep) -> Unit,
    onPlatformSelected: (PhonePlatform) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else DisabledAlpha),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        SetupStep.entries.forEach { step ->
            if (step != SetupStep.entries.first()) {
                Box(Modifier.width(StepperLineWidth).height(2.dp).background(FloconTheme.colors.lineStrong))
            }
            StepperItem(
                number = step.ordinal + 1,
                label = stringResource(step.label),
                done = enabled && step.ordinal < current.ordinal,
                current = enabled && step == current,
                onClick = { if (enabled) onStepSelected(step) },
            )
        }
        Box(Modifier.weight(1f))
        FloconSegmentedTextToggle(
            options = persistentListOf(
                stringResource(Res.string.proxy_platform_android),
                stringResource(Res.string.proxy_platform_ios),
            ),
            selectedIndex = platform.ordinal,
            onSelect = { onPlatformSelected(PhonePlatform.entries[it]) },
        )
    }
}

private val SetupStep.label: StringResource
    get() = when (this) {
        SetupStep.Wifi -> Res.string.proxy_step_wifi_label
        SetupStep.Certificate -> Res.string.proxy_step_certificate_label
        SetupStep.App -> Res.string.proxy_step_app_label
    }

@Composable
private fun StepperItem(
    number: Int,
    label: String,
    done: Boolean,
    current: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val color = when {
        done -> colors.statusSuccess
        current -> colors.accent
        else -> colors.inkFaint
    }
    Row(
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .clickable(onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = FloconTheme.spacing.extraSmall, vertical = FloconTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Box(
            modifier = Modifier
                .size(StepNumberSize)
                .then(
                    if (done || current) {
                        Modifier.background(color, CircleShape)
                    } else {
                        Modifier.border(1.dp, colors.lineStrong, CircleShape)
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (done) {
                FloconIcon(
                    imageVector = Icons.Outlined.Check,
                    tint = colors.bg000,
                    modifier = Modifier.size(StepCheckSize),
                )
            } else {
                Text(
                    text = number.toString(),
                    style = FloconTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (current) colors.accentInk else colors.inkFaint,
                )
            }
        }
        Text(
            text = label,
            style = FloconTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = color,
        )
    }
}

/** The proxy is off: what it does, and a big button to turn it on. */
@Composable
private fun OffPanel(
    onActivate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        Text(
            text = stringResource(Res.string.proxy_off_title),
            style = FloconTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FloconTheme.colors.ink,
        )
        Text(
            text = stringResource(Res.string.proxy_intro),
            style = FloconTheme.typography.bodyMedium,
            color = FloconTheme.colors.inkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = IntroMaxWidth),
        )
        FloconButton(
            onClick = onActivate,
            containerColor = FloconTheme.colors.accent,
            modifier = Modifier
                .padding(top = FloconTheme.spacing.small)
                .height(ActivateButtonHeight)
                .pointerHoverIcon(PointerIcon.Hand),
        ) {
            Text(
                text = stringResource(Res.string.proxy_activate),
                style = FloconTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = FloconTheme.colors.accentInk,
                modifier = Modifier.padding(horizontal = FloconTheme.spacing.large),
            )
        }
    }
}

@Composable
private fun Message(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = FloconTheme.colors.ink,
) {
    Text(
        text = text,
        style = FloconTheme.typography.bodyMedium,
        color = color,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

/** One step of the setup, for the chosen platform. */
@Composable
private fun StepContent(
    step: SetupStep,
    platform: PhonePlatform,
    addresses: ImmutableList<ProxyAddressUiModel>,
    modifier: Modifier = Modifier,
) {
    var selectedIp by remember { mutableStateOf<String?>(null) }
    val address = addresses.firstOrNull { it.ip == selectedIp } ?: addresses.first()
    val android = platform == PhonePlatform.Android

    Box(modifier = modifier.fillMaxSize()) {
        when (step) {
            SetupStep.Wifi -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraLarge),
            ) {
                // what the proxy settings of the phone look like once filled, at the place of the QR code of the next step
                Column(
                    modifier = Modifier.width(WifiCardWidth),
                    verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                ) {
                    WifiSettingsCard(address = address)
                    // the computer may be on several networks: the phone uses the one of its Wi-Fi
                    if (addresses.size > 1) {
                        NetworkPicker(
                            addresses = addresses,
                            selectedIp = address.ip,
                            onSelect = { selectedIp = it },
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium)) {
                    StepHeading(text = stringResource(Res.string.proxy_wifi_heading))
                    val steps = if (android) {
                        listOf(Res.string.proxy_android_wifi_1, Res.string.proxy_android_wifi_2, Res.string.proxy_wifi_3)
                    } else {
                        listOf(Res.string.proxy_ios_wifi_1, Res.string.proxy_ios_wifi_2, Res.string.proxy_wifi_3)
                    }
                    steps.forEachIndexed { index, text ->
                        NumberedStep(number = index + 1, text = stringResource(text))
                    }
                }
            }

            SetupStep.Certificate -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraLarge),
            ) {
                QrCodeImage(
                    content = address.caUrl,
                    modifier = Modifier
                        .size(QrSize)
                        .clip(FloconTheme.shapes.large),
                )
                Column(verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium)) {
                    StepHeading(text = stringResource(Res.string.proxy_certificate_heading))
                    val steps = if (android) {
                        listOf(Res.string.proxy_android_step_download, Res.string.proxy_android_step_install)
                    } else {
                        listOf(Res.string.proxy_ios_step_download, Res.string.proxy_ios_step_install, Res.string.proxy_ios_step_trust)
                    }
                    steps.forEachIndexed { index, text ->
                        NumberedStep(number = index + 1, text = stringResource(text))
                    }
                    CopyableValue(label = null, value = address.caUrl)
                }
            }

            SetupStep.App -> Column(verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium)) {
                StepHeading(text = stringResource(Res.string.proxy_app_heading))
                if (android) {
                    StepText(text = stringResource(Res.string.proxy_android_step_app))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(FloconTheme.colors.bg000, FloconTheme.shapes.medium)
                            .padding(FloconTheme.spacing.small),
                    ) {
                        Text(
                            text = USER_CERTIFICATES_SNIPPET,
                            style = FloconTheme.typography.monoMedium,
                            color = FloconTheme.colors.ink,
                            modifier = Modifier.weight(1f),
                        )
                        CopyButton(value = USER_CERTIFICATES_SNIPPET)
                    }
                } else {
                    StepText(text = stringResource(Res.string.proxy_ios_step_app))
                }
            }
        }
    }
}

/** Disable on the left, Back / Next (Done on the last step) on the right. */
@Composable
private fun Footer(
    enabled: Boolean,
    canNavigate: Boolean,
    step: SetupStep,
    onDisable: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val last = step == SetupStep.entries.last()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(FloconTheme.colors.bg000)
            .padding(horizontal = FloconTheme.spacing.extraLarge, vertical = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        if (enabled) {
            FooterButton(text = stringResource(Res.string.proxy_disable), primary = false, onClick = onDisable)
        }
        Box(Modifier.weight(1f))
        Row(
            modifier = Modifier.alpha(if (canNavigate) 1f else DisabledAlpha),
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            if (step != SetupStep.entries.first()) {
                FooterButton(
                    text = stringResource(Res.string.proxy_back),
                    primary = false,
                    onClick = { if (canNavigate) onBack() },
                )
            }
            FooterButton(
                text = stringResource(if (last) Res.string.proxy_done else Res.string.proxy_next),
                primary = canNavigate,
                onClick = {
                    when {
                        !canNavigate -> Unit
                        last -> onDone()
                        else -> onNext()
                    }
                },
            )
        }
    }
}

@Composable
private fun FooterButton(
    text: String,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (primary) {
        FloconButton(
            onClick = onClick,
            containerColor = FloconTheme.colors.accent,
            modifier = modifier.pointerHoverIcon(PointerIcon.Hand),
        ) {
            Text(
                text = text,
                style = FloconTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = FloconTheme.colors.accentInk,
            )
        }
    } else {
        FloconOutlinedButton(
            onClick = onClick,
            modifier = modifier.pointerHoverIcon(PointerIcon.Hand),
        ) {
            Text(
                text = text,
                style = FloconTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = FloconTheme.colors.ink,
            )
        }
    }
}

/** The proxy settings of the phone, filled: Manual, the server and the port, each with a copy button. */
@Composable
private fun WifiSettingsCard(
    address: ProxyAddressUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(FloconTheme.colors.bg000, FloconTheme.shapes.large)
            .border(1.dp, FloconTheme.colors.line, FloconTheme.shapes.large),
    ) {
        SettingsRow(label = stringResource(Res.string.proxy_wifi_proxy_label), value = stringResource(Res.string.proxy_wifi_manual), copyable = false)
        Box(Modifier.fillMaxWidth().height(1.dp).background(FloconTheme.colors.line))
        SettingsRow(label = stringResource(Res.string.proxy_server), value = address.ip, copyable = true)
        Box(Modifier.fillMaxWidth().height(1.dp).background(FloconTheme.colors.line))
        SettingsRow(label = stringResource(Res.string.proxy_port), value = address.proxyPort, copyable = true)
    }
}

@Composable
private fun SettingsRow(
    label: String,
    value: String,
    copyable: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(SettingsRowHeight)
            .padding(start = FloconTheme.spacing.medium, end = FloconTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.inkMuted,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = if (copyable) FloconTheme.typography.monoMedium else FloconTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (copyable) FloconTheme.colors.ink else FloconTheme.colors.accent,
            maxLines = 1,
            modifier = Modifier.padding(end = if (copyable) 0.dp else FloconTheme.spacing.small),
        )
        if (copyable) CopyButton(value = value)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NetworkPicker(
    addresses: ImmutableList<ProxyAddressUiModel>,
    selectedIp: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // wraps: the computer may be on more networks than the column has room for
    FlowRow(
        modifier = modifier,
        itemVerticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Text(
            text = stringResource(Res.string.proxy_pick_network),
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.inkMuted,
        )
        addresses.forEach { address ->
            val selected = address.ip == selectedIp
            val shape = FloconTheme.shapes.medium
            Text(
                text = address.ip,
                style = FloconTheme.typography.monoMedium,
                fontWeight = FontWeight.SemiBold,
                color = FloconTheme.colors.ink,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .clip(shape)
                    .background(if (selected) FloconTheme.colors.accentSoft else FloconTheme.colors.bg000)
                    .border(1.dp, if (selected) FloconTheme.colors.accent.copy(alpha = 0.4f) else FloconTheme.colors.line, shape)
                    .clickable { onSelect(address.ip) }
                    .pointerHoverIcon(PointerIcon.Hand)
                    .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.extraSmall),
            )
        }
    }
}

@Composable
private fun NumberedStep(
    number: Int,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Text(
            text = number.toString(),
            style = FloconTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = FloconTheme.colors.accentInk,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .size(StepNumberSize)
                .background(FloconTheme.colors.accent, CircleShape),
        )
        Text(
            text = text,
            style = FloconTheme.typography.bodyMedium,
            color = FloconTheme.colors.ink,
        )
    }
}

@Composable
private fun StepHeading(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = FloconTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = FloconTheme.colors.ink,
        modifier = modifier,
    )
}

@Composable
private fun StepText(
    text: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
) {
    Text(
        text = text,
        style = FloconTheme.typography.bodySmall,
        color = FloconTheme.colors.inkMuted,
        minLines = minLines,
        modifier = modifier,
    )
}

@Composable
private fun CopyButton(
    value: String,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current
    FloconIconButton(
        imageVector = Icons.Outlined.ContentCopy,
        onClick = { clipboardManager.setText(AnnotatedString(value)) },
        tooltip = stringResource(Res.string.proxy_copy),
        modifier = modifier,
    )
}

/** A value to type on the phone, with a button to copy it (to send it to the phone, for instance). */
@Composable
private fun CopyableValue(
    label: String?,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        if (label != null) {
            Text(
                text = label,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colors.inkMuted,
                textAlign = TextAlign.End,
                modifier = Modifier.width(LabelWidth),
            )
        }
        Text(
            text = value,
            style = FloconTheme.typography.monoMedium,
            fontWeight = FontWeight.Bold,
            color = FloconTheme.colors.ink,
        )
        CopyButton(value = value)
    }
}

@Preview
@Composable
private fun ProxyDialogRunningPreview() {
    FloconTheme {
        ProxyDialog(
            uiState = previewProxyUiState(),
            onEnabledChange = {},
            onDismissRequest = {},
        )
    }
}

@Preview
@Composable
private fun ProxyDialogOffPreview() {
    FloconTheme {
        ProxyDialog(
            uiState = previewProxyUiState(status = ProxyStatusUiModel.Off),
            onEnabledChange = {},
            onDismissRequest = {},
        )
    }
}

@Preview
@Composable
private fun ProxyDialogBinaryNotFoundPreview() {
    FloconTheme {
        ProxyDialog(
            uiState = previewProxyUiState(status = ProxyStatusUiModel.BinaryNotFound),
            onEnabledChange = {},
            onDismissRequest = {},
        )
    }
}
