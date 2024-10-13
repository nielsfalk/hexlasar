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
            glow()
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
                if (state.grid.solvedAndLocked) nextGrid()
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

            NextGrid -> {
                nextGrid()
            }

            LevelUp -> {
                state.update {
                    val levelType = state.levelType.next()
                    it.copy(
                        grid = newGrid(
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
                onEvent(NextGrid)
            }

            Hint -> {
                if (!state.grid.solved) {
                    state.grid.getPendingCell()?.let {
                        val position = it.position
                        updateCell(position) { cell ->
                            cell.copy(locked = true)
                        }
                        val pendingRotations =
                            (Direction.entries.size - (it.rotations % Direction.entries.size)) % it.connections.scrambleAmount
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
                            .removePrismaGlow(event.position)
                            .removeDisconnectedFromPaths()
                    }
                    viewModelScope.launch {
                        afterRotation(event.position)
                    }
                }
            }
        }
    }

    private fun nextGrid() {
        updateGrid { newGrid(state.levelType, state.toggleXYWithLevelGeneration) }
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
                            .removePrismaGlow(position)
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
        val cell = state.grid[position]
        if (cell.locked) { // happening on doubletabbing the last rotation on iphone
            updateCell(position) { it.copy(locked = false) }
            viewModelScope.launch {
                delay(200)
                updateCell(position) { cell.copy(locked = true) }
            }
        }
        if (state.grid.solved && state.animationSpendTime == null) {
            winning()
        }
    }

    private suspend fun winning() {
        state.update {
            it.copy(
                grid = it.grid.update(it.grid.cells.map { cell -> cell.copy(locked = true) }),
                solvingCount = it.solvingCount + it.levelType
            )
        }
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

private operator fun Map<LevelType, Int>.plus(levelType: LevelType): Map<LevelType, Int> =
    this + (levelType to this[levelType]!! + 1)

private fun Grid.getPendingCell(): Cell? =
    cells.filter { it.rotations % it.connections.scrambleAmount != 0 && it.locked }.randomOrNull()
        ?: cells.filter { it.rotations % it.connections.scrambleAmount != 0 && it.endPoint.isEmpty() }.randomOrNull()
        ?: cells.filter { it.rotations % it.connections.scrambleAmount != 0 }.randomOrNull()

const val animationSpeed = 3000

fun newGrid(
    levelType: LevelType = LevelType.entries.first(),
    toggleXYWithLevelGeneration: Boolean = false
) =
    GridGenerator(
        levelType = levelType,
        levelProperties = levelType.levelProperties.random().let {
            if (toggleXYWithLevelGeneration)
                it.copy(x = it.y, y = it.x)
            else it
        }
    )
        .generate()
        .initGlowPath()
