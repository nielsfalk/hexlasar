package de.nielsfalk.laserhexagon

import androidx.compose.runtime.*
import de.nielsfalk.laserhexagon.GameEvent.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun GameViewModel(): ViewModel<GameState, GameEvent> {
    val viewModelScope = rememberCoroutineScope()

    @Suppress("LocalVariableName")
    var state by remember { mutableStateOf(GameState(newLevel())) }

    return object : ViewModel<GameState, GameEvent>(state, { state = it }) {


        private fun updateGrid(function: (Grid) -> Grid) {
            state.update { it.copy(grid = function(it.grid)) }
        }

        private suspend fun glow() {
            var ongoing = true
            while (ongoing) {
                updateGrid {
                    val oldState = it
                    val newState = it.followPath()
                    ongoing = oldState != newState
                    newState
                }
                delay(glowSpeed.milliseconds)
            }
        }

        override fun onEvent(event: GameEvent) {
            when (event) {
                is RotateCell -> {
                    if (!state.grid[event.cellPosition].locked) {
                        viewModelScope.launch {
                            (1..rotationSpeed).forEach { idx ->
                                val isLast = idx == rotationSpeed
                                updateGrid {
                                    val cell = it[event.cellPosition]
                                    val rotated = it.update(
                                        if (isLast) {
                                            cell.copy(
                                                rotations = cell.rotations + 1,
                                                rotatedParts = cell.rotatedParts + 1 - rotationSpeed
                                            )
                                        } else {
                                            cell.copy(rotatedParts = cell.rotatedParts + 1)
                                        }
                                    )
                                    if (idx == 1 || isLast) {
                                        rotated.removeDisconnectedFromPaths()
                                    } else rotated
                                }
                                if (isLast) {
                                    glow()
                                }
                                if (state.grid.solved) {
                                    updateGrid(Grid::lockAllCells)
                                }
                                delay(1)
                            }
                        }
                    }
                }

                is LockCell -> {
                    updateGrid {
                        it.update(it[event.cellPosition].toggleLock())
                    }
                }

                Retry -> {
                    updateGrid { it.reset() }
                    viewModelScope.launch {
                        glow()
                    }
                }

                Next -> {
                    updateGrid { newLevel(state.levelType, state.toggleXYWithLevelGeneration) }
                    viewModelScope.launch {
                        glow()
                    }
                }

                LevelUp -> {
                    state.update {
                        val levelType = state.levelType.next()
                        it.copy(
                            grid = newLevel(
                                levelType = levelType,
                                toggleXYWithLevelGeneration = state.toggleXYWithLevelGeneration
                            ),
                            levelType = levelType
                        )
                    }
                    viewModelScope.launch {
                        glow()
                    }
                }

                is ToggleXYWithLevelGeneration -> {
                    state.update {
                        state.copy(toggleXYWithLevelGeneration = event.toggle)
                    }
                    onEvent(Next)
                }
            }
        }
    }
}

fun newLevel(
    levelType: LevelType = LevelType.entries.first(),
    toggleXYWithLevelGeneration: Boolean = false
) =
    LevelGenerator(
        levelType = levelType,
        levelProperties = levelType.levelProperties.random().let {
            if (toggleXYWithLevelGeneration)
                it.copy(x = it.y, y = it.x)
            else it
        }
    )
        .generate()
        .initGlowPath()
