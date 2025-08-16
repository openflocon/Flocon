package io.github.openflocon.flocon

object Protocol {
    object FromDevice {

        object Analytics {
            const val Plugin = "Analytics"

            object Method {
                const val AddItems = "AddItems"
            }
        }

        object Dashboard {
            const val Plugin = "Dashboard"

            object Method {
                const val Update = "update"
            }
        }

        object Device {
            const val Plugin = "device"

            object Method {
                const val RegisterDevice = "registerDevice"
            }
        }

        object Database {
            const val Plugin = "database"

            object Method {
                const val Query = "query"
                const val GetDatabases = "getDatabases"
            }
        }

        object Deeplink {
            const val Plugin = "Deeplink"

            object Method {
                const val GetDeeplinks = "GetDeeplinks"
            }
        }

        object Files {
            const val Plugin = "files"

            object Method {
                const val ListFiles = "listFiles"
            }
        }

        object Images {
            const val Plugin = "images"

            object Method {
                const val LogNetworkImage = "logNetworkCall"
            }
        }

        object Network {
            const val Plugin = "network"

            object Method {
                const val LogNetworkCallRequest = "logNetworkCallRequest"
                const val LogNetworkCallResponse = "logNetworkCallResponse"
            }
        }

        object SharedPreferences {
            const val Plugin = "sharedPreferences"

            object Method {
                const val GetSharedPreferences = "getSharedPreferences"
                const val GetSharedPreferenceValue = "getSharedPreferenceValue"
            }
        }

        object Table {
            const val Plugin = "Table"

            object Method {
                const val AddItems = "AddItems"
            }
        }


    }

    object ToDevice {

        object Analytics {
            const val Plugin = "Analytics"

            object Method {
                const val ClearItems = "ClearItems"
            }
        }

        object Dashboard {
            const val Plugin = "Dashboard"

            object Method {
                const val OnClick = "onClick"
                const val OnTextFieldSubmitted = "onTextFieldSubmitted"
                const val OnCheckBoxValueChanged = "onCheckBoxValueChanged"
            }
        }

        object Database {
            const val Plugin = "database"

            object Method {
                const val Query = "query"
                const val GetDatabases = "getDatabases"
            }
        }

        object Files {
            const val Plugin = "files"

            object Method {
                const val ListFiles = "listFiles"
                const val DeleteFile = "deleteFile"
                const val DeleteFolderContent = "deleteFolderContent"
            }
        }

        object Network {
            const val Plugin = "network"

            object Method {
                const val SetupMocks = "setupMocks"
                const val SetupBadNetworkConfig = "setupBadNetworkConfig"
            }
        }

        object SharedPreferences {
            const val Plugin = "sharedPreferences"

            object Method {
                const val GetSharedPreferences = "getSharedPreferences"
                const val GetSharedPreferenceValue = "getSharedPreferenceValue"
                const val SetSharedPreferenceValue = "setSharedPreferenceValue"
            }
        }

        object Table {
            const val Plugin = "Table"

            object Method {
                const val ClearItems = "ClearItems"
            }
        }
    }
}
