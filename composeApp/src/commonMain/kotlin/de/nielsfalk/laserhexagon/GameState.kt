package de.nielsfalk.laserhexagon

data class GameState(
    val grid: Grid,
    val toggleXYWithLevelGeneration:Boolean=false,
    val levelType: LevelType= LevelType.entries.first(),
    val solvingAnimationSpendTime: Int? =null
)