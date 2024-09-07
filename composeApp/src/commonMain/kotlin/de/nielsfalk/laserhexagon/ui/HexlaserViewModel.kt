package de.nielsfalk.laserhexagon.ui

import de.nielsfalk.laserhexagon.*
import de.nielsfalk.laserhexagon.ui.HexlaserEvent.*
import de.nielsfalk.util.TimingContext.Companion.repeatWithTiming
import de.nielsfalk.util.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class HexlaserViewModel : ViewModel<HexLaserState, HexlaserEvent>() {
    override fun onInitialized() {
        viewModelScope.launch {
            animation()
        }
    }

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
            is TabCell -> {
                if (state.grid.solvedAndLocked) nextLevel()
                else launchRotation(event.cellPosition)
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

            NextLevel -> {
                nextLevel()
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
                onEvent(NextLevel)
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
                                rotateDelayed(position)
                            }
                        }
                    }
                }
            }

            is DragCell -> {
                val cell = state.grid[event.position]
                if (!cell.locked) {
                    updateGrid {
                        it.update(cell.copy(rotations = cell.rotations + event.rotations))
                            .removeDisconnectedFromPaths()
                    }
                    viewModelScope.launch {
                        afterRotation(event.position)
                    }
                }
            }
        }
    }

    private fun nextLevel() {
        updateGrid { newLevel(state.levelType, state.toggleXYWithLevelGeneration) }
        viewModelScope.launch {
            glow()
        }
    }

    private fun launchRotation(position: Position) {
        if (!state.grid[position].locked) {
            viewModelScope.launch {
                rotateDelayed(position)
            }
        }
    }

    private suspend fun rotateDelayed(position: Position) {
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
                    afterRotation(position)
                } else {
                    updateCell(position) {
                        it.copy(rotatedParts = it.rotatedParts + delta)
                    }
                }

            }
            !lastIteration
        }
    }

    private suspend fun afterRotation(position: Position) {
        glow()
        if (state.grid[position].locked) { // happening on doubletabbing the last rotation on iphone
            updateCell(position) { it.copy(locked = false) }
        }
        if (state.grid.solved && state.animationSpendTime == null) {
            winning()
        }
    }

    private suspend fun winning() {
        updateGrid(Grid::lockAllCells)
        animation()
    }

    private suspend fun animation() {
        repeatWithTiming {
            state.update {
                it.copy(animationSpendTime = spendTime)
            }
            delay(1)
            spendTime < animationSpeed
        }
        state.update {
            it.copy(animationSpendTime = null)
        }
    }
}

private fun Grid.getPendingCell(): Cell? =
    cells.filter { it.rotations % Direction.entries.size != 0 && it.locked }.randomOrNull()
        ?: cells.filter { it.rotations % Direction.entries.size != 0 && it.endPoint.isEmpty() }.randomOrNull()
        ?: cells.filter { it.rotations % Direction.entries.size != 0 }.randomOrNull()

const val animationSpeed = 3000

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
