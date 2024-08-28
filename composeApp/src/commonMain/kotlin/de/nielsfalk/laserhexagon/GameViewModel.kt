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
                    (1..rotationSpeed).forEachIndexed { _, idx ->
                        _state.update {
                            val cell = it[event.cellPosition]
                            cell.rotatedParts += 1
                            if (idx == rotationSpeed) {
                                cell.rotatedParts -= rotationSpeed
                                cell.rotations += 1
                            }
                            it
                        }
                        delay(1)
                    }
                }
        }
    }
}

val Cell.rotationWithParts: Float get() = rotations + rotatedParts / rotationSpeed.toFloat()
const val rotationSpeed = 200




