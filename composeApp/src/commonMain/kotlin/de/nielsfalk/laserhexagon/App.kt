package de.nielsfalk.laserhexagon

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = GameViewModel()


        GameScreen(
            state = viewModel.state,
            onEvent = viewModel::onEvent,
        )
    }
}
