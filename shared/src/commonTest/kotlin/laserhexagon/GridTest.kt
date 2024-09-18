package laserhexagon

import de.nielsfalk.laserhexagon.Grid
import de.nielsfalk.laserhexagon.get
import de.nielsfalk.laserhexagon.reset
import io.kotest.core.spec.style.FreeSpec
import kotlin.test.assertEquals

class GridTest : FreeSpec({
    "reset" {
        val grid = Grid(1, 1).let { it.update(it[0, 0].copy(rotations = 1, rotatedParts = 1)) }

            .reset()

        assertEquals(grid.cells, listOf(grid[0, 0].copy(rotations = 0, rotatedParts = 0)))
    }
})
