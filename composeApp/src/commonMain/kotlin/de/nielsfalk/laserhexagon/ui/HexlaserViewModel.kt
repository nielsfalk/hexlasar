package de.nielsfalk.laserhexagon.ui

import androidx.lifecycle.viewModelScope
import de.nielsfalk.laserhexagon.*
import de.nielsfalk.laserhexagon.Grid
import de.nielsfalk.laserhexagon.GridCache.get
import de.nielsfalk.laserhexagon.ui.HexlaserEvent.*
import de.nielsfalk.util.TimingContext.Companion.repeatWithTiming
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class HexlaserViewModel : androidx.lifecycle.ViewModel() {
    private val _state = MutableStateFlow(HexLaserState(newGrid()))
    val state: StateFlow<HexLaserState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            animation()
            glow()
        }
    }

    private fun updateGrid(function: (Grid) -> Grid) {
        _state.update {
            it.copy(grid = function(it.grid))
        }
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

    fun onEvent(event: HexlaserEvent) {
        when (event) {
            is TabCell -> {
                if (state.value.grid.solvedAndLocked) nextGrid()
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
                _state.update {
                    val levelType = it.levelType.next()
                    it.copy(
                        grid = newGrid(
                            levelType = levelType,
                            toggleXYWithLevelGeneration = it.toggleXYWithLevelGeneration
                        ),
                        levelType = levelType
                    )
                }
                viewModelScope.launch {
                    glow()
                }
            }

            is ToggleXYWithLevelGeneration -> {
                _state.update {
                    it.copy(toggleXYWithLevelGeneration = event.toggle)
                }
                onEvent(NextGrid)
            }

            Hint -> {
                if (!state.value.grid.solved) {
                    state.value.grid.getPendingCell()?.let {
                        val position = it.position
                        updateCell(position) { cell ->
                            cell.copy(locked = true)
                        }
                        repeat(it.pendingRotations) {
                            viewModelScope.launch {
                                rotateDelayed(position)
                            }
                        }
                    }
                }
            }

            is DragCell -> {
                val cell = state.value.grid[event.position]
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
        updateGrid { newGrid(state.value.levelType, state.value.toggleXYWithLevelGeneration) }
        viewModelScope.launch {
            glow()
        }
    }

    private fun launchRotation(position: Position) {
        if (!state.value.grid[position].locked) {
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
        val cell = state.value.grid[position]
        if (cell.locked) { // happening on doubletabbing the last rotation on iphone
            updateCell(position) { it.copy(locked = false) }
            viewModelScope.launch {
                delay(200)
                updateCell(position) { cell.copy(locked = true) }
            }
        }
        if (state.value.grid.solved && state.value.animationSpendTime == null) {
            winning()
        }
    }

    private suspend fun winning() {
        _state.update {
            it.copy(
                grid = it.grid.update(it.grid.cells.map { cell -> cell.copy(locked = true) }),
                solvingCount = it.solvingCount + it.levelType
            )
        }
        animation()
    }

    private suspend fun animation() {
        repeatWithTiming {
            _state.update {
                it.copy(animationSpendTime = spendTime)
            }
            delay(1)
            spendTime < animationSpeed
        }
        _state.update {
            it.copy(animationSpendTime = null)
        }
    }

    private fun newGrid(
        levelType: LevelType = LevelType.entries.first(),
        toggleXYWithLevelGeneration: Boolean = false
    ) = viewModelScope.get(
        levelType = levelType,
        toggleXY = toggleXYWithLevelGeneration
    )
}

private operator fun Map<LevelType, Int>.plus(levelType: LevelType): Map<LevelType, Int> =
    this + (levelType to this[levelType]!! + 1)

private fun Grid.getPendingCell(): Cell? =
    cells.filter {
        it.connections.isNotEmpty() &&
                it.connections.size != Direction.entries.size &&
                it.rotations % it.connections.scrambleAmount != 0 &&
                it.rotatedParts == 0
    }
        .run {
            filter { it.locked }.randomOrNull()
                ?: filter { it.endPoint.isEmpty() }.randomOrNull()
                ?: randomOrNull()
        }

const val animationSpeed = 3000
