package io.github.openflocon.flocondesktop.common.utils

import co.touchlab.kermit.Logger
import java.awt.Desktop
import java.io.File
import java.io.IOException

object OpenFile {
    fun openFileOnDesktop(path: String) {
        val file = File(path)

        if (!file.exists()) {
            Logger.e("❌file does not exists : " + file.getAbsolutePath())
            return
        }

        if (!Desktop.isDesktopSupported()) {
            Logger.e("❌ open not supported") // maybe use either / failue
            return
        }
        val desktop = Desktop.getDesktop()

        if (desktop.isSupported(Desktop.Action.OPEN)) {
            try {
                desktop.open(file)
                println("📂 Fichier ouvert avec l'application par défaut : " + file.getAbsolutePath())
            } catch (e: IOException) {
                Logger.e("💥 Erreur lors de l'ouverture : " + e.message)
            }
        } else {
            System.err.println("❌ L'action OPEN n'est pas supportée sur ce système.")
        }
    }
}
