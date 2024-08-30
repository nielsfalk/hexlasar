package de.nielsfalk.laserhexagon

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import de.nielsfalk.laserhexagon.GameEvent.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class GameViewModel : dev.icerock.moko.mvvm.viewmodel.ViewModel() {
    private val _state: MutableStateFlow<Grid> = MutableStateFlow(newLevel())
    val state: StateFlow<Grid> get() = _state
    var cellCenterPoints = mapOf<Offset, Position>() // will be leaked while drawing
    var canvasSize: Size? = null
    val widthGtHeight get() = canvasSize?.let { it.width > it.height } ?: false

    init {
        _state.update { it.initGlowPath() }
        viewModelScope.launch {
            glow()
        }
    }

    private suspend fun glow() {
        var ongoing = true
        while (ongoing) {
            _state.update {
                val oldState = it
                val newState = it.followPath()
                ongoing = oldState != newState
                newState
            }
            delay(glowSpeed.milliseconds)
        }
    }

    fun onEvent(event: GameEvent) {
        when (event) {
            is CanvasTab ->
                cellCenterPoints.cellCloseTo(event.offset)
                    ?.let { cellPosition ->
                        if (!state.value[cellPosition].locked) {
                            viewModelScope.launch {
                                (1..rotationSpeed).forEach { idx ->
                                    val isLast = idx == rotationSpeed
                                    _state.update {
                                        val cell = it[cellPosition]
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
                                    if (state.value.solved) {
                                        _state.update(Grid::lockAllCells)
                                    }
                                    delay(1)
                                }
                            }
                        }
                    }

            is CanvasLongPress -> {
                cellCenterPoints.cellCloseTo(event.offset)
                    ?.let { cellPosition ->
                        _state.update {
                            it.update(it[cellPosition].toggleLock())
                        }
                    }
            }

            Retry -> {
                _state.update { it.reset() }
                viewModelScope.launch {
                    glow()
                }
            }

            Next -> {
                _state.update { newLevel(it.levelType) }
                viewModelScope.launch {
                    glow()
                }
            }

            GameEvent.LevelUp -> {
                _state.update {
                    newLevel(it.levelType.next()).copy(

                    )
                }
            }
        }
    }

    private fun newLevel(levelType: LevelType = LevelType.entries.first()) =
        LevelGenerator(
            levelType = levelType,
            levelProperties = levelType.levelProperties.random().let {
                if (widthGtHeight)
                    it.copy(x = it.y, y = it.x)
                else it
            }
        )
            .generate()
            .initGlowPath()
}
