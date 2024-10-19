package de.nielsfalk.laserhexagon.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import de.nielsfalk.util.ViewModel.Companion.getViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = getViewModel(
            factoryMethod = { HexlaserViewModel() },
            initialState = HexLaserState(newGrid())
        )

        HexlaserScreen(
            state = viewModel.state,
            onEvent = viewModel::onEvent,
        )
    }
}
