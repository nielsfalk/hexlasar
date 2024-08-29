package de.nielsfalk.laserhexagon

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(testGrid: Grid) : dev.icerock.moko.mvvm.viewmodel.ViewModel() {
    private val _state: MutableStateFlow<Grid> = MutableStateFlow(testGrid)
    val state: StateFlow<Grid> get() = _state

    fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.Rotate ->
                viewModelScope.launch {
                    (1..rotationSpeed).forEach { idx ->
                        _state.update {
                            val cell = it[event.cellPosition]
                            it.update(
                                if (idx == rotationSpeed) {
                                    println("almost ")
                                    cell.copy(
                                        rotations = cell.rotations + 1,
                                        rotatedParts = cell.rotatedParts + 1 - rotationSpeed
                                    )
                                } else cell.copy(rotatedParts = cell.rotatedParts + 1)
                            )
                        }
                        delay(1)
                    }
                }
        }
    }
}
