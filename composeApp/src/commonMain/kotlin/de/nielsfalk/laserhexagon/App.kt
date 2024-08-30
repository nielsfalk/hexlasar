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
            factory = viewModelFactory { GameViewModel() }
        )
        val state: Grid by viewModel.state.collectAsState()

        GameScreen(
            onCanvasTab = { viewModel.onEvent(CanvasTab(it)) },
            onCanvasLongPress = { viewModel.onEvent(CanvasLongPress(it)) },
            onRetry = { viewModel.onEvent(Retry) },
            onNext = { viewModel.onEvent(Next) },
            onLevelUp = { viewModel.onEvent(LevelUp) },
            leakCellCenterPoints = { viewModel.cellCenterPoints = it },
            state = state,
            toggleXYWithLevelGeneration = { viewModel.onEvent(ToggleXYWithLevelGeneration(it)) },
        )
    }
}

