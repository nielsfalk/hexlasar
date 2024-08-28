package de.nielsfalk.laserhexagon

import de.nielsfalk.laserhexagon.Direction.BOTTOMLEFT
import de.nielsfalk.laserhexagon.Direction.BOTTOMRIGHT
import de.nielsfalk.laserhexagon.Direction.LEFT
import de.nielsfalk.laserhexagon.Direction.RIGHT
import de.nielsfalk.laserhexagon.Direction.TOPLEFT
import de.nielsfalk.laserhexagon.Direction.TOPRIGHT
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe

class CellTest : FreeSpec({
    "on a 5*5 grid" - {
        val grid = Grid(5, 6)

        "has 30 cells" {
            grid.cells.size shouldBe 30
        }

        "neighbours" - {
            "topleft cell has correct neighbours" {
                grid[0, 0].neighborsPositions shouldContainExactly mapOf(
                    RIGHT to Position(1, 0),
                    BOTTOMRIGHT to Position(0, 1)
                )
            }
            "topright cell has correct neighbours" {
                grid[4, 0].neighborsPositions shouldContainExactly mapOf(
                    LEFT to Position(3, 0),
                    BOTTOMLEFT to Position(3, 1),
                    BOTTOMRIGHT to Position(4, 1)
                )
            }
            "bottonleft cell has correct neighbours" {
                grid[0, 5].neighborsPositions shouldContainExactly mapOf(
                    TOPLEFT to Position(0, 4),
                    TOPRIGHT to Position(1, 4),
                    RIGHT to Position(1, 5)
                )
            }
            "bottonright cell has correct neighbours" {
                grid[4, 5].neighborsPositions shouldContainExactly mapOf(
                    LEFT to Position(3, 5),
                    TOPLEFT to Position(4, 4)
                )
            }
            "random cell in odd row has correct neighbours" {
                grid[3, 3].neighborsPositions shouldContainExactly mapOf(
                    LEFT to Position(2, 3),
                    TOPLEFT to Position(3, 2),
                    TOPRIGHT to Position(4, 2),
                    RIGHT to Position(4, 3),
                    BOTTOMRIGHT to Position(4, 4),
                    BOTTOMLEFT to Position(3, 4)
                )
            }
            "random cell in even row has correct neighbours" {
                grid[3, 4].neighborsPositions shouldContainExactly mapOf(
                    LEFT to Position(2, 4),
                    TOPLEFT to Position(2, 3),
                    TOPRIGHT to Position(3, 3),
                    RIGHT to Position(4, 4),
                    BOTTOMRIGHT to Position(3, 5),
                    BOTTOMLEFT to Position(2, 5)
                )
            }
        }
        "connected neighbours" - {
            "middle to right cell" {
                val modifiedGrid = grid.update(
                    grid[1,2].copy(connections = setOf(RIGHT)),
                    grid[2,2].copy(connections = setOf(RIGHT)),
                    grid[3,2].copy(connections = setOf(LEFT))
                )

                val cell = modifiedGrid[2,2]

                cell.connectedNeighbors shouldContainExactly mapOf(RIGHT to modifiedGrid[3,2])
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
        ).forEach{(given, expectedOpposite)->
            "$given has opposite $expectedOpposite"{
                given.opposite shouldBe expectedOpposite
            }
        }
        listOf(
            LEFT to TOPLEFT,
            TOPLEFT to TOPRIGHT,
            TOPRIGHT to RIGHT,
            RIGHT to BOTTOMRIGHT,
            BOTTOMRIGHT to BOTTOMLEFT,
            BOTTOMLEFT to LEFT,
        ).forEach{(given, expected)->
            "$given rotated is $expected"{
                given.rotate(1) shouldBe expected
            }
        }
    }
})
