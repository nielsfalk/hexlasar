package de.nielsfalk.laserhexagon.ui

import androidx.compose.runtime.*
import de.nielsfalk.laserhexagon.*
import de.nielsfalk.laserhexagon.ui.HexlaserEvent.*
import de.nielsfalk.laserhexagon.ui.TimingContext.Companion.repeatWithTiming
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun HexlaserViewModel(): ViewModel<HexLaserState, HexlaserEvent> {
    val viewModelScope = rememberCoroutineScope()

    var state by remember { mutableStateOf(HexLaserState(newLevel())) }

    return object : ViewModel<HexLaserState, HexlaserEvent>(state, { state = it }) {


        private fun updateGrid(function: (Grid) -> Grid) {
            state.update { it.copy(grid = function(it.grid)) }
        }

        private fun updateCell(position: Position, function: (Cell) -> Cell) {
            updateGrid {
                it.update(function(it[position]))
            }
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

        override fun onEvent(event: HexlaserEvent) {
            when (event) {
                is RotateCell -> {
                    launchRotation(event.cellPosition)
                }

                is LockCell -> {
                    updateCell(event.cellPosition) { it.toggleLock() }
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

                Hint -> {
                    if (!state.grid.solved) {
                        state.grid.getPendingCell()?.let {
                            val position = it.position
                            updateCell(position) { cell ->
                                cell.copy(locked = true)
                            }
                            val pendingRotations = Direction.entries.size - (it.rotations % Direction.entries.size)
                            repeat(pendingRotations) {
                                viewModelScope.launch {
                                    rotate(position)
                                }
                            }
                        }
                    }
                }
            }
        }

        private fun launchRotation(position: Position) {
            if (!state.grid[position].locked) {
                viewModelScope.launch {
                    rotate(position)
                }
            }
        }

        private suspend fun rotate(position: Position) {
            updateGrid {
                val cell = it[position]
                it.update(
                    cell.copy(rotatedParts = cell.rotatedParts + 1)
                )
                    .removeDisconnectedFromPaths()
            }
            repeatWithTiming {
                val lastIteration = spendTime >= rotationSpeed
                if (delta == 0)
                    delay(1)
                else {
                    if (lastIteration) {
                        updateGrid {
                            val cell = it[position]
                            it.update(
                                cell.copy(
                                    rotatedParts = cell.rotatedParts + delta - spendTime - 1,
                                    rotations = cell.rotations + 1
                                )
                            )
                                .removeDisconnectedFromPaths()
                        }
                        delay(1)
                        glow()
                        if (state.grid.solved) {
                            winning()
                        }
                    } else {
                        updateCell(position) {
                            it.copy(rotatedParts = it.rotatedParts + delta)
                        }
                    }

                }
                !lastIteration
            }
        }

        private suspend fun winning() {
            updateGrid(Grid::lockAllCells)
            repeatWithTiming {
                state.update {
                    it.copy(solvingAnimationSpendTime = spendTime)
                }
                delay(1)
                spendTime < winningAnimationSpeed
            }
            state.update {
                it.copy(solvingAnimationSpendTime = null)
            }
        }
    }
}

private fun Grid.getPendingCell(): Cell? =
    cells.filter { it.rotations % Direction.entries.size != 0 && it.locked }.randomOrNull()
        ?: cells.filter { it.rotations % Direction.entries.size != 0 && it.endPoint.isEmpty() }.randomOrNull()
        ?: cells.filter { it.rotations % Direction.entries.size != 0 }.randomOrNull()

const val winningAnimationSpeed = 3000

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
