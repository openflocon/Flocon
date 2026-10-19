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
                const val AddItems = "additems"
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
                const val RegisterDevice = "registerdevice"
                const val AppIcon = "appicon"
            }
        }

        object Database {
            const val Plugin = "database"

            object Method {
                const val Query = "query"
                const val GetDatabases = "getdatabases"
                const val LogQuery = "logquery"
            }
        }

        object Deeplink {
            const val Plugin = "deeplink"

            object Method {
                const val GetDeeplinks = "getdeeplinks"
            }
        }

        object Files {
            const val Plugin = "files"

            object Method {
                const val ListFiles = "listfiles"
            }
        }

        object Images {
            const val Plugin = "images"

            object Method {
                const val LogNetworkImage = "lognetworkcall"
            }
        }

        object Network {
            const val Plugin = "network"

            object Method {
                const val LogNetworkCallRequest = "lognetworkcallrequest"
                const val LogNetworkCallResponse = "lognetworkcallresponse"
                const val LogWebSocketEvent = "logwebsocketevent"
                const val RegisterWebSocketIds = "registerwebsocketids"
            }
        }

        object SharedPreferences {
            const val Plugin = "sharedpreferences"

            object Method {
                const val GetSharedPreferences = "getsharedpreferences"
                const val GetSharedPreferenceValue = "getsharedpreferencevalue"
            }
        }

        object Table {
            const val Plugin = "table"

            object Method {
                const val AddItems = "additems"
            }
        }

        object CrashReporter {
            const val Plugin = "crashreporter"

            object Method {
                const val ReportCrash = "reportcrash"
            }
        }

    }

    object ToDevice {

        object Dashboard {
            const val Plugin = "Dashboard"

            object Method {
                const val OnClick = "onClick"
                const val OnFormSubmitted = "onFormSubmitted"
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

        object Device {
            const val Plugin = "device"

            object Method {
                const val GetAppIcon = "getAppIcon"
                const val RestartApp = "restartApp"
            }
        }

        object Files {
            const val Plugin = "files"

            object Method {
                const val ListFiles = "listFiles"
                const val DeleteFile = "deleteFile"
                const val DeleteFiles = "deleteFiles"
                const val DeleteFolderContent = "deleteFolderContent"
                const val GetFile = "getFile"
            }
        }

        object Network {
            const val Plugin = "network"

            object Method {
                const val SetupMocks = "setupMocks"
                const val SetupBadNetworkConfig = "setupBadNetworkConfig"
                const val WebsocketMockMessage = "websocketMockMessage"
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

            object Method
        }
    }
}
