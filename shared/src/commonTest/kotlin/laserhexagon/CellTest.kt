package laserhexagon

import de.nielsfalk.laserhexagon.Color
import de.nielsfalk.laserhexagon.Direction.*
import de.nielsfalk.laserhexagon.Grid
import de.nielsfalk.laserhexagon.Position
import de.nielsfalk.laserhexagon.get
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlin.test.assertEquals

class CellTest : FreeSpec({
    "on a 5*5 grid" - {
        val grid = Grid(5, 6)

        "has 30 cells" {
            assertEquals(grid.cells.size, 30)
        }

        "neighbours" - {
            "topleft cell has correct neighbours" {
                assertEquals(
                    grid[0, 0].neighborsPositions,
                    mapOf(
                        RIGHT to Position(1, 0),
                        BOTTOMRIGHT to Position(0, 1)
                    )

                )
            }
            "topright cell has correct neighbours" {
                assertEquals(
                    grid[4, 0].neighborsPositions, mapOf(
                        LEFT to Position(3, 0),
                        BOTTOMLEFT to Position(3, 1),
                        BOTTOMRIGHT to Position(4, 1)
                    )
                )
            }
            "bottonleft cell has correct neighbours" {
                assertEquals(
                    grid[0, 5].neighborsPositions, mapOf(
                        TOPLEFT to Position(0, 4),
                        TOPRIGHT to Position(1, 4),
                        RIGHT to Position(1, 5)
                    )
                )
            }
            "bottonright cell has correct neighbours" {
                assertEquals(
                    grid[4, 5].neighborsPositions, mapOf(
                        LEFT to Position(3, 5),
                        TOPLEFT to Position(4, 4)
                    )
                )
            }
            "random cell in odd row has correct neighbours" {
                assertEquals(
                    grid[3, 3].neighborsPositions, mapOf(
                        LEFT to Position(2, 3),
                        TOPLEFT to Position(3, 2),
                        TOPRIGHT to Position(4, 2),
                        RIGHT to Position(4, 3),
                        BOTTOMRIGHT to Position(4, 4),
                        BOTTOMLEFT to Position(3, 4)
                    )
                )
            }
            "random cell in even row has correct neighbours" {
                assertEquals(
                    grid[3, 4].neighborsPositions, mapOf(
                        LEFT to Position(2, 4),
                        TOPLEFT to Position(2, 3),
                        TOPRIGHT to Position(3, 3),
                        RIGHT to Position(4, 4),
                        BOTTOMRIGHT to Position(3, 5),
                        BOTTOMLEFT to Position(2, 5)
                    )
                )
            }
            "infinite grid" - {
                "x" {
                    val infiniteXGrid = Grid(5, 6, infiniteX = true)

                    assertEquals(infiniteXGrid[0, 0].neighbors.size, 4)
                    assertEquals(infiniteXGrid[infiniteXGrid.x - 1, 0].neighbors.size, 4)
                    assertEquals(infiniteXGrid[0, infiniteXGrid.y - 1].neighbors.size, 4)
                    assertEquals(infiniteXGrid[infiniteXGrid.x - 1, infiniteXGrid.y - 1].neighbors.size, 4)
                }
                "y" {
                    val infiniteYGrid = Grid(5, 5, infiniteY = true)

                    assertEquals(infiniteYGrid[0, 0].neighbors.size, 3)
                    assertEquals(infiniteYGrid[infiniteYGrid.x - 1, 0].neighbors.size, 5)
                    assertEquals(infiniteYGrid[0, infiniteYGrid.y - 1].neighbors.size, 3)
                    assertEquals(infiniteYGrid[infiniteYGrid.x - 1, infiniteYGrid.y - 1].neighbors.size, 5)
                }
                "xy" {
                    val infiniteGrid = Grid(5, 6, infiniteX = true, infiniteY = true)

                    assertEquals(infiniteGrid[0, 0].neighbors.size, 6)
                    assertEquals(infiniteGrid[infiniteGrid.x - 1, 0].neighbors.size, 6)
                    assertEquals(infiniteGrid[0, infiniteGrid.y - 1].neighbors.size, 6)
                    assertEquals(infiniteGrid[infiniteGrid.x - 1, infiniteGrid.y - 1].neighbors.size, 6)
                }
            }
        }
        "connected neighbours" - {
            "middle to right cell" {
                val modifiedGrid = grid.update(
                    grid[1, 2].copy(connections = setOf(RIGHT)),
                    grid[2, 2].copy(connections = setOf(RIGHT)),
                    grid[3, 2].copy(connections = setOf(LEFT))
                )

                val cell = modifiedGrid[2, 2]

                assertEquals(cell.connectedNeighbors, mapOf(RIGHT to modifiedGrid[3, 2]))
            }
            "left and right" {
                val grid = Grid(3, 1).let {
                    it.update(
                        it[0, 0].copy(
                            source = Color.Orange,
                            connections = setOf(RIGHT)
                        ),
                        it[1, 0].copy(
                            connections = setOf(LEFT, RIGHT)
                        ),
                        it[2, 0].copy(
                            connections = setOf(LEFT)
                        )
                    )
                }

                assertEquals(
                    grid[1, 0].connectedNeighbors, mapOf(
                        LEFT to grid[0, 0],
                        RIGHT to grid[2, 0]
                    )
                )
            }
        }
    }
    "Direction" - {
        listOf(
            LEFT to RIGHT,
            TOPLEFT to BOTTOMRIGHT,
            TOPRIGHT to BOTTOMLEFT,
            RIGHT to LEFT,
            BOTTOMRIGHT to TOPLEFT,
            BOTTOMLEFT to TOPRIGHT,
        ).forEach { (given, expectedOpposite) ->
            "$given has opposite $expectedOpposite" {
                given.opposite shouldBe expectedOpposite
            }
            "$given +3 is $expectedOpposite" {
                (given + 3) shouldBe expectedOpposite
            }
        }
        listOf(
            LEFT to TOPLEFT,
            TOPLEFT to TOPRIGHT,
            TOPRIGHT to RIGHT,
            RIGHT to BOTTOMRIGHT,
            BOTTOMRIGHT to BOTTOMLEFT,
            BOTTOMLEFT to LEFT,
        ).forEach { (given, expected) ->
            "$given rotated by 1 is $expected" {
                given.rotate(1) shouldBe expected
            }
        }
    }
})

