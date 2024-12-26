package de.nielsfalk.laserhexagon.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = viewModel { HexlaserViewModel() }

        HexlaserScreen(
            state = viewModel.state.collectAsState().value,
            onEvent = viewModel::onEvent,
        )
    }
}
