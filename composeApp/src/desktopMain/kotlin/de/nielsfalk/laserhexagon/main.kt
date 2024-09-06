package de.nielsfalk.laserhexagon

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.nielsfalk.laserhexagon.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Hexlaser",
    ) {
        App()
    }
}