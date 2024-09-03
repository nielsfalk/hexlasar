package de.nielsfalk.laserhexagon

import de.nielsfalk.laserhexagon.Color.Red
import de.nielsfalk.laserhexagon.Color.Yellow
import de.nielsfalk.laserhexagon.Direction.LEFT
import de.nielsfalk.laserhexagon.Direction.RIGHT
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class GlowPathTest : FreeSpec({
    "remove disconnected" {
        val grid = Grid(3, 1).let {
            it.update(
                it[0, 0].copy(
                    source = Red,
                    connections = setOf(RIGHT)
                ),
                it[1, 0].copy(
                    connections = setOf(LEFT, RIGHT)
                ),
                it[2, 0].copy(
                    connections = setOf(LEFT)
                )
            )
        }.initGlowPath().followPathComplete()
            .let { it.update(it[1, 0].copy(rotations = 1)) }

            .removeDisconnectedFromPaths()

        grid.glowPath shouldBe GlowPath(
            sources = listOf(
                GlowPathEntry(
                    position = Position(x = 0, y = 0),
                    color = Red,
                )
            )
        )
    }

    "remove disconnected with partial rotation" {
        val grid = Grid(3, 1).let {
            it.update(
                it[0, 0].copy(
                    source = Red,
                    connections = setOf(RIGHT)
                ),
                it[1, 0].copy(
                    connections = setOf(LEFT, RIGHT)
                ),
                it[2, 0].copy(
                    connections = setOf(LEFT)
                )
            )
        }.initGlowPath().followPathComplete()
            .let { it.update(it[1, 0].copy(rotatedParts = 5)) }

            .removeDisconnectedFromPaths()

        grid.glowPath shouldBe GlowPath(
            sources = listOf(
                GlowPathEntry(
                    position = Position(x = 0, y = 0),
                    color = Red,
                )
            )
        )
    }

    "basic path" {
        val grid = Grid(3, 1).let {
            it.update(
                it[0, 0].copy(
                    source = Red,
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

            .initGlowPath().followPathComplete().removeDisconnectedFromPaths()

        grid.glowPath shouldBe GlowPath(
            sources = listOf(
                GlowPathEntry(
                    position = Position(x = 0, y = 0),
                    color = Red,
                    children = listOf(
                        GlowPathEntry(
                            position = Position(x = 1, y = 0),
                            parentPostition = Position(x = 0, y = 0),
                            color = Red,
                            children = listOf(
                                GlowPathEntry(
                                    position = Position(x = 2, y = 0),
                                    parentPostition = Position(x = 1, y = 0),
                                    color = Red,
                                    children = listOf()
                                )
                            )
                        )
                    )
                )
            )
        )
    }
    "get Colors for position" {
        val grid = Grid(3, 1).let {
            it.update(
                it[0, 0].copy(
                    source = Red,
                    connections = setOf(RIGHT)
                ),
                it[1, 0].copy(
                    connections = setOf(LEFT, RIGHT)
                ),
                it[2, 0].copy(
                    connections = setOf(LEFT),
                    source = Yellow
                )
            )
        }

            .initGlowPath().followPathComplete().removeDisconnectedFromPaths()

        grid.glowPath.colors(1, 0) shouldContainExactly setOf(Red, Yellow)
    }
    "not solved"{
        Grid(3, 1).let {
            it.update(
                it[0, 0].copy(
                    source = Red,
                    connections = setOf(RIGHT)
                ),
                it[1, 0].copy(
                    connections = setOf(LEFT, RIGHT),
                    endPoint = setOf(Red, Yellow)
                ),
                it[2, 0].copy(
                    connections = setOf(),
                    source = Yellow
                )
            )
        }.initGlowPath().followPathComplete()

            .solved shouldBe false
    }
    "solved"{
        Grid(3, 1).let {
            it.update(
                it[0, 0].copy(
                    source = Red,
                    connections = setOf(RIGHT)
                ),
                it[1, 0].copy(
                    connections = setOf(LEFT, RIGHT),
                    endPoint = setOf(Red, Yellow)
                ),
                it[2, 0].copy(
                    connections = setOf(LEFT),
                    source = Yellow
                )
            )
        }.initGlowPath().followPathComplete()

            .solved shouldBe true
    }
})
