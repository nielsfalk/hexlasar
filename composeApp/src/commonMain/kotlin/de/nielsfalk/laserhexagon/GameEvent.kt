package de.nielsfalk.laserhexagon

sealed interface GameEvent {
    data class Rotate(val cellPosition:Position):GameEvent
    data object Retry:GameEvent
    data object Next:GameEvent
}