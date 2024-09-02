package de.nielsfalk.laserhexagon.ui

import de.nielsfalk.laserhexagon.Grid
import de.nielsfalk.laserhexagon.LevelType

data class HexLaserState(
    val grid: Grid,
    val toggleXYWithLevelGeneration:Boolean=false,
    val levelType: LevelType = LevelType.entries.first(),
    val solvingAnimationSpendTime: Int? =null
)