package de.nielsfalk.laserhexagon

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

class GridTest : FreeSpec({
    "reset" {
        val grid = Grid(1, 1).let { it.update(it[0, 0].copy(rotations = 1, rotatedParts = 1)) }

            .reset()

        grid.cells shouldContainExactly listOf(grid[0, 0].copy(rotations = 0, rotatedParts = 0))
    }
})
