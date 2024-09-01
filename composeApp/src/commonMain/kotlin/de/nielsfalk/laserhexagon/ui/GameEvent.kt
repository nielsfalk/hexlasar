package de.nielsfalk.laserhexagon.ui

import de.nielsfalk.laserhexagon.Position

sealed interface GameEvent {
    data class RotateCell(val cellPosition: Position): GameEvent
    data class LockCell(val cellPosition: Position): GameEvent
    data object Retry: GameEvent
    data object Next: GameEvent
    data object LevelUp: GameEvent
    data class ToggleXYWithLevelGeneration(val toggle:Boolean): GameEvent
}