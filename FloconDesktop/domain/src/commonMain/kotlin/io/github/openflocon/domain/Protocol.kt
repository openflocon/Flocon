@file:Suppress("ktlint")
package io.github.openflocon.domain

object Protocol {
    object FromDevice {
        // lowercase: incoming plugin and method names are lowercased when a message is received

        // first message sent by the SDK when it connects, with the installed plugins
        object Bonjour {
            const val Plugin = "bonjour"

            object Method {
                const val Bonjour = "bonjour"
            }
        }

        object Analytics {
            const val Plugin = "analytics"

            object Method {
                const val AddItems = "add_items"
            }
        }

        object Accessibility {
            const val Plugin = "accessibility"

            object Method {
                const val AddEvents = "add_events"
                const val State = "state"
                const val FocusedNode = "focused_node"
            }
        }

        object FeatureFlags {
            const val Plugin = "feature_flags"

            object Method {
                const val Register = "register"
            }
        }

        object Dashboard {
            const val Plugin = "dashboard"

            object Method {
                const val Update = "update"
            }
        }

        object Device {
            const val Plugin = "device"

            object Method {
                const val RegisterDevice = "register_device"
                const val AppIcon = "app_icon"
            }
        }

        object Database {
            const val Plugin = "database"

            object Method {
                const val Query = "query"
                const val GetDatabases = "get_databases"
                const val LogQuery = "log_query"
            }
        }

        object Deeplink {
            const val Plugin = "deeplink"

            object Method {
                const val GetDeeplinks = "get_deeplinks"
            }
        }

        object Files {
            const val Plugin = "files"

            object Method {
                const val ListFiles = "list_files"
                const val GetRoots = "get_roots"
            }
        }

        object Images {
            const val Plugin = "images"

            object Method {
                const val LogNetworkImage = "log_network_call"
            }
        }

        object Network {
            const val Plugin = "network"

            object Method {
                const val LogNetworkCallRequest = "log_network_call_request"
                const val LogNetworkCallResponse = "log_network_call_response"
                const val LogWebSocketEvent = "log_websocket_event"
                const val RegisterWebSocketIds = "register_websocket_ids"
            }
        }

        object SharedPreferences {
            const val Plugin = "shared_preferences"

            object Method {
                const val GetSharedPreferences = "get_shared_preferences"
                const val GetSharedPreferenceValue = "get_shared_preference_value"
            }
        }

        object Table {
            const val Plugin = "table"

            object Method {
                const val AddItems = "add_items"
            }
        }

        object CrashReporter {
            const val Plugin = "crash_reporter"

            object Method {
                const val ReportCrash = "report_crash"
            }
        }

        object WebView {
            const val Plugin = "webview"

            object Method {
                const val AddConsoleLogs = "add_console_logs"
                const val AddPageEvents = "add_page_events"
                const val AddBridgeCalls = "add_bridge_calls"
            }
        }

    }

    object ToDevice {

        object Accessibility {
            const val Plugin = "accessibility"

            object Method {
                const val GetFocusedNode = "get_focused_node"
                const val FocusNext = "focus_next"
                const val FocusPrevious = "focus_previous"
                const val Activate = "activate"
            }
        }

        object Dashboard {
            const val Plugin = "dashboard"

            object Method {
                const val OnClick = "on_click"
                const val OnFormSubmitted = "on_form_submitted"
                const val OnTextFieldSubmitted = "on_text_field_submitted"
                const val OnCheckBoxValueChanged = "on_check_box_value_changed"
            }
        }

        object Database {
            const val Plugin = "database"

            object Method {
                const val Query = "query"
                const val GetDatabases = "get_databases"
            }
        }

        object Device {
            const val Plugin = "device"

            object Method {
                const val GetAppIcon = "get_app_icon"
                const val RestartApp = "restart_app"
            }
        }

        object Files {
            const val Plugin = "files"

            object Method {
                const val ListFiles = "list_files"
                const val DeleteFile = "delete_file"
                const val DeleteFiles = "delete_files"
                const val DeleteFolderContent = "delete_folder_content"
                const val GetFile = "get_file"
                const val GetRoots = "get_roots"
            }
        }

        object Network {
            const val Plugin = "network"

            object Method {
                const val SetupMocks = "setup_mocks"
                const val SetupBadNetworkConfig = "setup_bad_network_config"
                const val WebsocketMockMessage = "websocket_mock_message"
            }
        }

        object SharedPreferences {
            const val Plugin = "shared_preferences"

            object Method {
                const val GetSharedPreferences = "get_shared_preferences"
                const val GetSharedPreferenceValue = "get_shared_preference_value"
                const val SetSharedPreferenceValue = "set_shared_preference_value"
                const val RemoveSharedPreferenceValue = "remove_shared_preference_value"
            }
        }

        object Table {
            const val Plugin = "table"

            object Method
        }
    }
}
