package de.nielsfalk.laserhexagon

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class GameViewModel(testGrid: Grid) : dev.icerock.moko.mvvm.viewmodel.ViewModel() {
    private val _state: MutableStateFlow<Grid> = MutableStateFlow(testGrid)
    val state: StateFlow<Grid> get() = _state

    init {
        _state.update { it.initGlowPath() }
        viewModelScope.launch {
            glow()
        }
    }

    suspend fun glow() {
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
            is GameEvent.Rotate ->
                viewModelScope.launch {
                    (1..rotationSpeed).forEach { idx ->
                        val isLast = idx == rotationSpeed
                        _state.update {
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
                        delay(1)
                    }
                }
        }
    }
}
