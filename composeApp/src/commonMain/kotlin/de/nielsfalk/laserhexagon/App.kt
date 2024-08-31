package de.nielsfalk.laserhexagon

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {

        val viewModel: GameViewModel = getViewModel(
            key = "gameViewModel",
            factory = viewModelFactory { GameViewModel() }
        )
        val state: Grid by viewModel.state.collectAsState()

        GameScreen(
            state = state,
            onEvent=  viewModel::onEvent,
        )
    }
}

