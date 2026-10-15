package io.github.openflocon.flocondesktop.common.utils

import co.touchlab.kermit.Logger
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.swing.JFileChooser

object FolderPicker {

    private const val TAG = "FolderPicker"

    fun pickFolder(
        title: String = "Select Folder",
        initialPath: String? = null,
    ): File? {
        val isMac = System.getProperty("os.name", "").lowercase().contains("mac")
        return if (isMac) {
            pickFolderMac(title, initialPath)
        } else {
            pickFolderSwing(title, initialPath)
        }
    }

    private fun pickFolderMac(
        title: String,
        initialPath: String?,
    ): File? {
        val previousProp = System.getProperty("apple.awt.fileDialogForDirectories")
        return try {
            System.setProperty("apple.awt.fileDialogForDirectories", "true")
            val frame = Frame()
            val dialog = FileDialog(frame, title, FileDialog.LOAD).apply {
                if (!initialPath.isNullOrBlank()) {
                    val initialFile = File(initialPath)
                    if (initialFile.exists()) {
                        directory = if (initialFile.isDirectory) initialFile.absolutePath else initialFile.parent
                    }
                }
            }
            dialog.isVisible = true
            val file = dialog.file
            val dir = dialog.directory
            frame.dispose()

            if (file == null) {
                Logger.d(TAG) { "Folder picking cancelled on macOS" }
                null
            } else {
                val selected = if (dir != null) File(dir, file) else File(file)
                Logger.d(TAG) { "Folder picked on macOS: ${selected.absolutePath}" }
                selected
            }
        } catch (t: Throwable) {
            Logger.e(TAG, t) { "Error while picking folder on macOS, falling back to Swing" }
            pickFolderSwing(title, initialPath)
        } finally {
            if (previousProp != null) {
                System.setProperty("apple.awt.fileDialogForDirectories", previousProp)
            } else {
                System.clearProperty("apple.awt.fileDialogForDirectories")
            }
        }
    }

    private fun pickFolderSwing(
        title: String,
        initialPath: String?,
    ): File? {
        return try {
            val chooser = JFileChooser().apply {
                dialogTitle = title
                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                isMultiSelectionEnabled = false
                if (!initialPath.isNullOrBlank()) {
                    val initialFile = File(initialPath)
                    if (initialFile.exists()) {
                        currentDirectory = if (initialFile.isDirectory) initialFile else initialFile.parentFile
                    }
                }
            }
            val result = chooser.showOpenDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                val selected = chooser.selectedFile
                Logger.d(TAG) { "Folder picked via Swing: ${selected?.absolutePath}" }
                selected
            } else {
                Logger.d(TAG) { "Folder picking cancelled on Swing" }
                null
            }
        } catch (t: Throwable) {
            Logger.e(TAG, t) { "Error while picking folder via Swing" }
            null
        }
    }
}
