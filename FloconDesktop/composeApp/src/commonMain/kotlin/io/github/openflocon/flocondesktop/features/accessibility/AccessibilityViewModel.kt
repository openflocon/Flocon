package io.github.openflocon.flocondesktop.features.accessibility

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.accessibility_remote_toggle_talkback_error
import io.github.openflocon.domain.accessibility.usecase.FocusNextAccessibilityNodeUseCase
import io.github.openflocon.domain.accessibility.usecase.FocusPreviousAccessibilityNodeUseCase
import io.github.openflocon.domain.accessibility.usecase.ObserveCurrentDeviceAccessibilityEventsUseCase
import io.github.openflocon.domain.accessibility.usecase.ObserveCurrentDeviceAccessibilityFocusedNodeUseCase
import io.github.openflocon.domain.accessibility.usecase.ObserveCurrentDeviceAccessibilityStateUseCase
import io.github.openflocon.domain.accessibility.usecase.ObserveCurrentDeviceAccessibilityTypesUseCase
import io.github.openflocon.domain.accessibility.usecase.RefreshAccessibilityFocusedNodeUseCase
import io.github.openflocon.domain.accessibility.usecase.ResetCurrentDeviceAccessibilityEventsUseCase
import io.github.openflocon.domain.accessibility.usecase.SetTalkBackEnabledUseCase
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.accessibility.mapper.mapToBannerUi
import io.github.openflocon.flocondesktop.features.accessibility.mapper.mapToUi
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityAction
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityBannerUiModel
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityFocusedNodeUiModel
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityRowUiModel
import io.github.openflocon.library.designsystem.common.asState
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class AccessibilityViewModel(
    private val dispatcherProvider: DispatcherProvider,
    observeCurrentDeviceAccessibilityEventsUseCase: ObserveCurrentDeviceAccessibilityEventsUseCase,
    observeCurrentDeviceAccessibilityTypesUseCase: ObserveCurrentDeviceAccessibilityTypesUseCase,
    observeCurrentDeviceAccessibilityStateUseCase: ObserveCurrentDeviceAccessibilityStateUseCase,
    observeCurrentDeviceAccessibilityFocusedNodeUseCase: ObserveCurrentDeviceAccessibilityFocusedNodeUseCase,
    private val focusNextAccessibilityNodeUseCase: FocusNextAccessibilityNodeUseCase,
    private val focusPreviousAccessibilityNodeUseCase: FocusPreviousAccessibilityNodeUseCase,
    private val setTalkBackEnabledUseCase: SetTalkBackEnabledUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val refreshAccessibilityFocusedNodeUseCase: RefreshAccessibilityFocusedNodeUseCase,
    private val resetCurrentDeviceAccessibilityEventsUseCase: ResetCurrentDeviceAccessibilityEventsUseCase,
    private val navigationState: MainFloconNavigationState,
) : ViewModel() {

    private val _filterText = mutableStateOf("")
    val filterText: State<String> = _filterText.asState()

    // null: all the types
    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType: StateFlow<String?> = _selectedType.asStateFlow()

    val types: StateFlow<ImmutableList<String>> = observeCurrentDeviceAccessibilityTypesUseCase()
        .map { it.toImmutableList() }
        .flowOn(dispatcherProvider.viewModel)
        .stateInWhileSubscribed(persistentListOf())

    val banner: StateFlow<AccessibilityBannerUiModel?> = observeCurrentDeviceAccessibilityStateUseCase()
        .map { it?.mapToBannerUi() }
        .flowOn(dispatcherProvider.viewModel)
        .stateInWhileSubscribed(null)

    // the focus can only be moved while a screen reader is running on the device
    val canMoveFocus: StateFlow<Boolean> = banner
        .map { it is AccessibilityBannerUiModel.Running }
        .stateInWhileSubscribed(false)

    // another service alone (Switch Access...) is not TalkBack
    val talkBackOn: StateFlow<Boolean> = banner
        .map { it is AccessibilityBannerUiModel.Running && it.services.any { id -> id.contains("talkback", ignoreCase = true) } }
        .stateInWhileSubscribed(false)

    // null: nothing has the accessibility focus
    val focusedNode: StateFlow<AccessibilityFocusedNodeUiModel?> = observeCurrentDeviceAccessibilityFocusedNodeUseCase()
        .map { it?.mapToUi() }
        .flowOn(dispatcherProvider.viewModel)
        .stateInWhileSubscribed(null)

    init {
        // the device only tells when the focus changes, ask where it is now
        onAction(AccessibilityAction.OnRefreshFocusedNodeClicked)
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val rows: StateFlow<ImmutableList<AccessibilityRowUiModel>> = combine(
        snapshotFlow { _filterText.value }.debounce(150),
        _selectedType,
    ) { text, type -> text to type }
        .flatMapLatest { (text, type) ->
            observeCurrentDeviceAccessibilityEventsUseCase(
                type = type,
                text = text.takeIf { it.isNotBlank() },
            )
        }
        .map { list -> list.map { it.mapToUi() }.toImmutableList() }
        .flowOn(dispatcherProvider.viewModel)
        .stateInWhileSubscribed(persistentListOf())

    fun onFilterTextChanged(value: String) {
        _filterText.value = value
    }

    fun onTypeSelected(type: String?) {
        _selectedType.value = type
    }

    fun onClearClicked() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            resetCurrentDeviceAccessibilityEventsUseCase()
        }
    }

    fun onAction(action: AccessibilityAction) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when (action) {
                is AccessibilityAction.OnClick -> navigationState.navigate(
                    AccessibilityRoutes.Detail(id = action.item.id)
                )

                AccessibilityAction.OnFocusNextClicked -> focusNextAccessibilityNodeUseCase()

                AccessibilityAction.OnFocusPreviousClicked -> focusPreviousAccessibilityNodeUseCase()

                is AccessibilityAction.OnTalkBackToggled -> setTalkBackEnabled(action.enabled)

                AccessibilityAction.OnRefreshFocusedNodeClicked -> refreshAccessibilityFocusedNodeUseCase()
            }
        }
    }

    // the device tells by itself once TalkBack changed, which updates the banner and so the switch
    private suspend fun setTalkBackEnabled(enabled: Boolean) {
        setTalkBackEnabledUseCase(enabled).alsoFailure {
            it.printStackTrace()
            feedbackDisplayer.displayMessage(
                message = getString(Res.string.accessibility_remote_toggle_talkback_error, it.message.orEmpty()),
                type = FeedbackDisplayer.MessageType.Error,
            )
        }
    }
}
