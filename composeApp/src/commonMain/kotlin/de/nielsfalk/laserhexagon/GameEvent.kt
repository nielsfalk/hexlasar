package de.nielsfalk.laserhexagon

import androidx.compose.ui.geometry.Offset

sealed interface GameEvent {
    data class CanvasTab(val offset: Offset):GameEvent
    data class CanvasLongPress(val offset: Offset):GameEvent
    data object Retry:GameEvent
    data object Next:GameEvent
    data object LevelUp:GameEvent
}