package de.nielsfalk.laserhexagon

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import de.nielsfalk.laserhexagon.GameEvent.*
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {

        val viewModel: GameViewModel = getViewModel(
            key = "gameViewModel",
            factory = viewModelFactory { GameViewModel(testGrid) }
        )
        val state: Grid by viewModel.state.collectAsState()

        GameScreen(
            onTabCell = { viewModel.onEvent(Rotate(it)) },
            onRetry = { viewModel.onEvent(Retry) },
            onNext = { viewModel.onEvent(Next) },
            state = state
        )
    }
}

