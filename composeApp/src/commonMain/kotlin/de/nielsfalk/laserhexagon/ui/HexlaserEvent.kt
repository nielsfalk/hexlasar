package de.nielsfalk.laserhexagon.ui

import de.nielsfalk.laserhexagon.Position

sealed interface HexlaserEvent {
    data class TabCell(val cellPosition: Position): HexlaserEvent
    data class DragCell(val rotations: Int, val position: Position): HexlaserEvent
    data class LockCell(val cellPosition: Position): HexlaserEvent
    data object Retry: HexlaserEvent
    data object NextGrid: HexlaserEvent
    data object LevelUp: HexlaserEvent
    data object Hint: HexlaserEvent
    data class ToggleXYWithLevelGeneration(val toggle:Boolean): HexlaserEvent
}