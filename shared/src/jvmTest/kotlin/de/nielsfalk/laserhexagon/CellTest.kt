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
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class CellTest : FreeSpec({
    "on a 5*5 grid" - {
        val grid = Grid(5, 6)

        "has 5 cells" {
            grid[0].size shouldBe 6
            grid.cells.flatten().size shouldBe 30
        }

        "neighbours" - {
            "topleft cell has correct neighbours" {
                grid[0][0].neighborsPositions shouldContainExactly mapOf(
                    RIGHT to Position(1, 0),
                    BOTTOMRIGHT to Position(0, 1)
                )
            }
            "topright cell has correct neighbours" {
                grid[4][0].neighborsPositions shouldContainExactly mapOf(
                    LEFT to Position(3, 0),
                    BOTTOMLEFT to Position(3, 1),
                    BOTTOMRIGHT to Position(4, 1)
                )
            }
            "bottonleft cell has correct neighbours" {
                grid[0][5].neighborsPositions shouldContainExactly mapOf(
                    TOPLEFT to Position(0, 4),
                    TOPRIGHT to Position(1, 4),
                    RIGHT to Position(1, 5)
                )
            }
            "bottonright cell has correct neighbours" {
                grid[4][5].neighborsPositions shouldContainExactly mapOf(
                    LEFT to Position(3, 5),
                    TOPLEFT to Position(4, 4)
                )
            }
            "random cell in odd row has correct neighbours" {
                grid[3][3].neighborsPositions shouldContainExactly mapOf(
                    LEFT to Position(2, 3),
                    TOPLEFT to Position(3, 2),
                    TOPRIGHT to Position(4, 2),
                    RIGHT to Position(4, 3),
                    BOTTOMRIGHT to Position(4, 4),
                    BOTTOMLEFT to Position(3, 4)
                )
            }
            "random cell in even row has correct neighbours" {
                grid[3][4].neighborsPositions shouldContainExactly mapOf(
                    LEFT to Position(2, 4),
                    TOPLEFT to Position(2, 3),
                    TOPRIGHT to Position(3, 3),
                    RIGHT to Position(4, 4),
                    BOTTOMRIGHT to Position(3, 5),
                    BOTTOMLEFT to Position(2, 5)
                )
            }
        }
        "rotation" - {
            listOf(0,1).forEach {
                "should not rotate for future events" {
                    val clock = FixedClock()
                    val cell = Grid(5, 6)[0][0].copy(clock = clock)

                    cell.rotate()
                    clock - it.milliseconds

                    cell.pendingRotationEdges() shouldBe 0f
                }
            }
            "should rotate for past events" {
                val clock = FixedClock()
                val cell = Grid(5, 6)[0][0].copy(clock = clock)

                cell.rotate()
                clock + 200.milliseconds

                cell.pendingRotationEdges() shouldBe 1f
            }
            "should sum rotations for past events" {
                val clock = FixedClock()
                val cell = Grid(5, 6)[0][0].copy(clock = clock)

                cell.rotate()
                cell.rotate()
                clock + 300.milliseconds

                cell.pendingRotationEdges() shouldBe 2f
            }
            "should rotate partial for past events" {
                val clock = FixedClock()
                val cell = Grid(5, 6)[0][0].copy(clock = clock)

                cell.rotate()
                clock + 50.milliseconds

                cell.pendingRotationEdges() shouldBe 0.25f
            }
        }
    }
})


data class FixedClock(var now: Instant = Clock.System.now()) : Clock {
    override fun now(): Instant = now
    operator fun plus(duration: Duration) {
        now += duration
    }

    operator fun minus(duration: Duration) {
        now-=duration
    }
}