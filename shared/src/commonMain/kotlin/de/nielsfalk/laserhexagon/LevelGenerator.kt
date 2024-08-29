package de.nielsfalk.laserhexagon

import kotlin.random.Random

class LevelGenerator(
    val x: Int = 4,
    val y: Int = 5,
    val sourceCount: Int = 3,
    val random: Random = Random.Default
) {
    fun generate(): Grid {
        var grid = Grid(x, y)

        repeat(sourceCount) {
            grid = grid.update(
                grid.cells.random(random).copy(
                    source = COLOR.random(random))
            )
        }

        return grid
    }
}

private fun COLOR.Companion.random(random: Random): COLOR =
    COLOR.entries[random.nextInt(COLOR.entries.size)]
