package io.github.openflocon.flocondesktop.features.onboarding.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.onboarding.OnboardingStep
import io.github.openflocon.flocondesktop.features.onboarding.OnboardingViewModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconCircularProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconCodeBlock
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconOutlinedButton
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.FloconTextButton
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material.icons.outlined.FolderOpen
import io.github.openflocon.flocondesktop.common.utils.pickAdbFile
import io.github.openflocon.library.designsystem.components.FloconIconButton
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween

@Composable
fun OnboardingScreen() {
    val viewModel = koinViewModel<OnboardingViewModel>()
    val currentStep by viewModel.currentStep.collectAsState()
    val adbPathInput by viewModel.adbPathInput.collectAsState()
    val adbValid by viewModel.adbValid.collectAsState()
    val isAdbTesting by viewModel.isAdbTesting.collectAsState()
    val successCountdown by viewModel.successCountdown.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FloconTheme.colorPalette.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .width(600.dp)
                .padding(32.dp)
        ) {
            // Header Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Welcome to Flocon",
                    style = FloconTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = FloconTheme.colorPalette.onAccent
                    )
                )

                if (currentStep != OnboardingStep.Success) {
                    FloconTextButton(
                        onClick = viewModel::skipOnboarding,
                        containerColor = Color.Transparent
                    ) {
                        Text(
                            text = "Close",
                            color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Step indicator
            StepIndicator(currentStep = currentStep)

            // Main wizard Card
            FloconSurface(
                color = FloconTheme.colorPalette.primary,
                shape = FloconTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(animationSpec = tween(durationMillis = 300))
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            (slideInHorizontally(animationSpec = tween(300)) { width -> width } + fadeIn(animationSpec = tween(300))) togetherWith
                                    (slideOutHorizontally(animationSpec = tween(300)) { width -> -width } + fadeOut(animationSpec = tween(300)))
                        } else {
                            (slideInHorizontally(animationSpec = tween(300)) { width -> -width } + fadeIn(animationSpec = tween(300))) togetherWith
                                    (slideOutHorizontally(animationSpec = tween(300)) { width -> width } + fadeOut(animationSpec = tween(300)))
                        }.using(
                            SizeTransform(clip = false)
                        )
                    }
                ) { step ->
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        when (step) {
                            OnboardingStep.AdbConfig -> AdbConfigStep(
                                adbPathInput = adbPathInput,
                                adbValid = adbValid,
                                isAdbTesting = isAdbTesting,
                                onPathChange = viewModel::onAdbPathChanged,
                                onTest = viewModel::testAdbPath,
                                onNext = viewModel::nextStep
                            )
                            OnboardingStep.FloconSetup -> FloconSetupStep(
                                onBack = viewModel::previousStep,
                                onNext = viewModel::nextStep
                            )
                            OnboardingStep.LaunchApp -> LaunchAppStep(
                                onBack = viewModel::previousStep
                            )
                            OnboardingStep.Success -> SuccessStep(
                                countdown = successCountdown
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: OnboardingStep) {
    val steps = listOf(
        "ADB Setup" to OnboardingStep.AdbConfig,
        "SDK Setup" to OnboardingStep.FloconSetup,
        "Launch App" to OnboardingStep.LaunchApp,
        "Connected" to OnboardingStep.Success
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, (label, step) ->
            val isActive = currentStep == step
            val isCompleted = currentStep.ordinal > step.ordinal

            val circleColor by animateColorAsState(
                targetValue = when {
                    isActive -> FloconTheme.colorPalette.accent
                    isCompleted -> FloconTheme.colorPalette.secondary.copy(alpha = 0.5f)
                    else -> FloconTheme.colorPalette.secondary.copy(alpha = 0.2f)
                },
                animationSpec = tween(durationMillis = 300)
            )

            val textColor by animateColorAsState(
                targetValue = if (isActive) FloconTheme.colorPalette.onAccent else FloconTheme.colorPalette.onSecondary,
                animationSpec = tween(durationMillis = 300)
            )

            val labelColor by animateColorAsState(
                targetValue = if (isActive) FloconTheme.colorPalette.onSurface else FloconTheme.colorPalette.onSurface.copy(alpha = 0.6f),
                animationSpec = tween(durationMillis = 300)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(circleColor)
                    ) {
                        if (isCompleted) {
                            FloconIcon(
                                imageVector = Icons.Default.Check,
                                tint = FloconTheme.colorPalette.onSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = (index + 1).toString(),
                                style = FloconTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = FloconTheme.typography.bodySmall.copy(
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = labelColor
                    ),
                    textAlign = TextAlign.Center
                )
            }
            if (index < steps.size - 1) {
                val lineColor by animateColorAsState(
                    targetValue = if (currentStep.ordinal > step.ordinal) FloconTheme.colorPalette.secondary.copy(alpha = 0.5f)
                    else FloconTheme.colorPalette.secondary.copy(alpha = 0.2f),
                    animationSpec = tween(durationMillis = 300)
                )
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .weight(0.5f)
                        .background(lineColor)
                )
            }
        }
    }
}

@Composable
private fun AdbConfigStep(
    adbPathInput: String,
    adbValid: Boolean?,
    isAdbTesting: Boolean,
    onPathChange: (String) -> Unit,
    onTest: () -> Unit,
    onNext: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Text(
        text = "1. ADB Configuration",
        style = FloconTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

    Text(
        text = "Flocon communicates with Android devices using the Android Debug Bridge (ADB). Please configure the path to your adb binary to proceed.",
        style = FloconTheme.typography.bodyMedium,
        color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.8f)
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "ADB Executable Path",
        style = FloconTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold
    )

    FloconTextField(
        value = adbPathInput,
        onValueChange = onPathChange,
        placeholder = { Text("Eg: /Users/youruser/Library/Android/sdk/platform-tools/adb") },
        trailingComponent = {
            FloconIconButton(
                onClick = {
                    scope.launch {
                        pickAdbFile()?.let { onPathChange(it) }
                    }
                }
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.FolderOpen,
                    tint = FloconTheme.colorPalette.onSecondary
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FloconButton(
            onClick = onTest,
            containerColor = FloconTheme.colorPalette.secondary,
            modifier = Modifier.height(36.dp)
        ) {
            if (isAdbTesting) {
                FloconCircularProgressIndicator(
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Testing...")
            } else {
                Text("Test Connection")
            }
        }

        when (adbValid) {
            true -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FloconIcon(
                        imageVector = Icons.Default.CheckCircle,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Connection Successful",
                        style = FloconTheme.typography.bodyMedium.copy(color = Color(0xFF4CAF50))
                    )
                }
            }
            false -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FloconIcon(
                        imageVector = Icons.Default.Error,
                        tint = FloconTheme.colorPalette.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Validation Failed",
                        style = FloconTheme.typography.bodyMedium.copy(color = FloconTheme.colorPalette.error)
                    )
                }
            }
            null -> {}
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        FloconButton(
            onClick = { if (adbValid == true) onNext() },
            containerColor = if (adbValid == true) FloconTheme.colorPalette.accent else FloconTheme.colorPalette.secondary.copy(alpha = 0.5f),
            modifier = Modifier.width(100.dp)
        ) {
            Text(
                text = "Next",
                color = if (adbValid == true) FloconTheme.colorPalette.onAccent else FloconTheme.colorPalette.onSecondary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            FloconIcon(
                imageVector = Icons.Default.ChevronRight,
                tint = if (adbValid == true) FloconTheme.colorPalette.onAccent else FloconTheme.colorPalette.onSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun FloconSetupStep(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Text(
        text = "2. Integrate Flocon SDK",
        style = FloconTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

    Text(
        text = "Add Flocon inside your Android app to start intercepting network requests, databases, and logs.",
        style = FloconTheme.typography.bodyMedium,
        color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.8f)
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 240.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Step A: Add Dependency",
            style = FloconTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        FloconCodeBlock(
            code = """
                // In your build.gradle.kts (app module):
                dependencies {
                    debugImplementation("io.github.openflocon:flocon:1.4.0")
                    releaseImplementation("io.github.openflocon:flocon-no-op:1.4.0")
                }
            """.trimIndent(),
            modifier = Modifier.fillMaxWidth(),
            containerColor = FloconTheme.colorPalette.secondary
        )

        Text(
            text = "Step B: Initialize in Application",
            style = FloconTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        FloconCodeBlock(
            code = """
                // In your Application or MainActivity onCreate():
                import io.github.openflocon.flocon.Flocon
                
                Flocon.initialize(context)
            """.trimIndent(),
            modifier = Modifier.fillMaxWidth(),
            containerColor = FloconTheme.colorPalette.secondary
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FloconOutlinedButton(
            onClick = onBack,
            modifier = Modifier.width(100.dp)
        ) {
            FloconIcon(
                imageVector = Icons.Default.ChevronLeft,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Back")
        }

        FloconButton(
            onClick = onNext,
            containerColor = FloconTheme.colorPalette.accent,
            modifier = Modifier.width(100.dp)
        ) {
            Text(
                text = "Next",
                color = FloconTheme.colorPalette.onAccent
            )
            Spacer(modifier = Modifier.width(4.dp))
            FloconIcon(
                imageVector = Icons.Default.ChevronRight,
                tint = FloconTheme.colorPalette.onAccent,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun LaunchAppStep(
    onBack: () -> Unit
) {
    Text(
        text = "3. Launch Your App",
        style = FloconTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

    Text(
        text = "Run your Android application on an emulator or a connected physical device.",
        style = FloconTheme.typography.bodyMedium,
        color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.8f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FloconCircularProgressIndicator(
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Waiting for Flocon app connection...",
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.7f)
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        FloconOutlinedButton(
            onClick = onBack,
            modifier = Modifier.width(100.dp)
        ) {
            FloconIcon(
                imageVector = Icons.Default.ChevronLeft,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Back")
        }
    }
}

@Composable
private fun SuccessStep(
    countdown: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FloconIcon(
                imageVector = Icons.Default.CheckCircle,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Successfully Connected!",
                style = FloconTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
            Text(
                text = "Flocon has successfully detected your application and is ready to capture network requests, databases, and logs.",
                style = FloconTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Redirecting to network menu in $countdown seconds...",
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
