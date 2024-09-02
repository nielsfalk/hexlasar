package de.nielsfalk.laserhexagon.ui

import de.nielsfalk.laserhexagon.Position

sealed interface HexlaserEvent {
    data class RotateCell(val cellPosition: Position): HexlaserEvent
    data class LockCell(val cellPosition: Position): HexlaserEvent
    data object Retry: HexlaserEvent
    data object Next: HexlaserEvent
    data object LevelUp: HexlaserEvent
    data object Hint: HexlaserEvent
    data class ToggleXYWithLevelGeneration(val toggle:Boolean): HexlaserEvent
}