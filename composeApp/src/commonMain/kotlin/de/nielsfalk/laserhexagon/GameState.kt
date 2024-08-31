package de.nielsfalk.laserhexagon

import androidx.compose.ui.geometry.Offset

data class GameState (
    val grid: Grid,
    val toggleXYWithLevelGeneration:Boolean=false,
    val levelType: LevelType= LevelType.entries.first()
)