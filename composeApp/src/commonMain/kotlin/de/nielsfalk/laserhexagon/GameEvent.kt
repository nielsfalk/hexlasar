package de.nielsfalk.laserhexagon

import androidx.compose.ui.geometry.Offset

sealed interface GameEvent {
    data class RotateCell(val offset: Offset):GameEvent
    data class LockCell(val offset: Offset):GameEvent
    data object Retry:GameEvent
    data object Next:GameEvent
    data object LevelUp:GameEvent
    data class ToggleXYWithLevelGeneration(val toggle:Boolean):GameEvent
    data class LeakCellPositions(val cellCenterPoints:Map<Offset, Position>):GameEvent
}