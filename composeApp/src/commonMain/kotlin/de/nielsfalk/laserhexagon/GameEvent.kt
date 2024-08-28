package de.nielsfalk.laserhexagon

sealed interface GameEvent {
    data class Rotate(val cellPosition:Position):GameEvent
}