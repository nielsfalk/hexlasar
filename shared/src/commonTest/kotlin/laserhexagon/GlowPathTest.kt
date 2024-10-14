package laserhexagon

import de.nielsfalk.laserhexagon.*
import de.nielsfalk.laserhexagon.Color.*
import de.nielsfalk.laserhexagon.Direction.*
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlin.test.assertEquals

class GlowPathTest : FreeSpec({
    "remove disconnected" {
        val grid = Grid(3, 1).let {
            it.update(
                it[0, 0].copy(
                    source = Orange,
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
                    color = Orange,
                )
            )
        )
    }

    "remove disconnected with partial rotation" {
        val grid = Grid(3, 1).let {
            it.update(
                it[0, 0].copy(
                    source = Orange,
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
                    color = Orange,
                )
            )
        )
    }

    "basic path" {
        val grid = Grid(3, 1).let {
            it.update(
                it[0, 0].copy(
                    source = Orange,
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
                    color = Orange,
                    children = listOf(
                        GlowPathEntry(
                            position = Position(x = 1, y = 0),
                            parentPosition = Position(x = 0, y = 0),
                            color = Orange,
                            children = listOf(
                                GlowPathEntry(
                                    position = Position(x = 2, y = 0),
                                    parentPosition = Position(x = 1, y = 0),
                                    color = Orange,
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
                    source = Orange,
                    connections = setOf(RIGHT)
                ),
                it[1, 0].copy(
                    connections = setOf(LEFT, RIGHT)
                ),
                it[2, 0].copy(
                    connections = setOf(LEFT),
                    source = Green
                )
            )
        }

            .initGlowPath().followPathComplete().removeDisconnectedFromPaths()

        assertEquals(grid.glowPath.colors(1, 0), setOf(Orange, Green))
    }
    "not solved" {
        Grid(3, 1).let {
            it.update(
                it[0, 0].copy(
                    source = Orange,
                    connections = setOf(RIGHT)
                ),
                it[1, 0].copy(
                    connections = setOf(LEFT, RIGHT),
                    endPoint = setOf(Orange, Green)
                ),
                it[2, 0].copy(
                    connections = setOf(),
                    source = Green
                )
            )
        }.initGlowPath().followPathComplete()

            .solved shouldBe false
    }
    "solved" {
        Grid(3, 1).let {
            it.update(
                it[0, 0].copy(
                    source = Orange,
                    connections = setOf(RIGHT)
                ),
                it[1, 0].copy(
                    connections = setOf(LEFT, RIGHT),
                    endPoint = setOf(Orange, Green)
                ),
                it[2, 0].copy(
                    connections = setOf(LEFT),
                    source = Green
                )
            )
        }.initGlowPath().followPathComplete()

            .solved shouldBe true
    }

    data class PrismaTestData(
        val prismaConnections: Set<Direction>,
        val expectedPrismaColors: Map<Direction, Color>? = null
    )
    listOf(
        PrismaTestData(
            prismaConnections = setOf(LEFT),
            expectedPrismaColors = mapOf(LEFT to Orange)
        ),
        PrismaTestData(
            prismaConnections = setOf(LEFT, RIGHT),
            expectedPrismaColors = mapOf(LEFT to Orange, RIGHT to Green)
        ),
        PrismaTestData(
            prismaConnections = setOf(LEFT, TOPLEFT, RIGHT, BOTTOMRIGHT),
            expectedPrismaColors = mapOf(LEFT to Orange, TOPLEFT to Green, RIGHT to Purple, BOTTOMRIGHT to Orange)
        ),
        PrismaTestData(
            prismaConnections = Direction.entries.toSet(),
            expectedPrismaColors = mapOf(
                LEFT to Orange,
                TOPLEFT to Green,
                TOPRIGHT to Purple,
                RIGHT to Orange,
                BOTTOMRIGHT to Green,
                BOTTOMLEFT to Purple
            )
        )
    ).forEach { testData ->
        testData.run {
            "prisma with $prismaConnections" {
                val grid = Grid(2, 1).let {
                    it.update(
                        it[0, 0].copy(source = Orange, connections = setOf(RIGHT)),
                        it[1, 0].copy(prisma = true, connections = prismaConnections),
                    )
                }

                val glowPath = grid.initGlowPath().followPathComplete().glowPath

                assertEquals(
                    actual = glowPath,
                    expected = GlowPath(
                        sources = listOf(
                            GlowPathEntry(
                                position = Position(x = 0, y = 0),
                                parentPosition = null,
                                color = Orange,
                                children = listOf(
                                    GlowPathEntry(
                                        position = Position(x = 1, y = 0),
                                        parentPosition = Position(x = 0, y = 0),
                                        color = Orange,
                                        prismaColors = expectedPrismaColors
                                    )
                                ),
                                prismaColors = null
                            )
                        )
                    )
                )
            }
        }
    }
    "prisma to Prisma" {
        val grid = Grid(4, 1).let {
            it.update(
                it[0, 0].copy(source = Orange, connections = setOf(RIGHT)),
                it[1, 0].copy(prisma = true, connections = setOf(RIGHT, LEFT)),
                it[2, 0].copy(prisma = true, connections = setOf(RIGHT, LEFT)),
                it[3, 0].copy(source = Purple, connections = setOf(LEFT))
            )
        }

        val glowPath = grid.initGlowPath().followPathComplete().glowPath

        assertEquals(
            actual = glowPath,
            expected = GlowPath(
                sources = listOf(
                    GlowPathEntry(
                        position = Position(x = 0, y = 0),
                        parentPosition = null,
                        color = Orange,
                        children = listOf(
                            GlowPathEntry(
                                position = Position(x = 1, y = 0),
                                parentPosition = Position(x = 0, y = 0),
                                color = Orange,
                                children = listOf(
                                    GlowPathEntry(
                                        position = Position(x = 2, y = 0),
                                        parentPosition = Position(x = 1, y = 0),
                                        color = Green,
                                        children = listOf(
                                            GlowPathEntry(
                                                position = Position(x = 3, y = 0),
                                                parentPosition = Position(x = 2, y = 0),
                                                color = Purple,
                                                children = listOf(),
                                                prismaColors = null
                                            )
                                        ),
                                        prismaColors = mapOf( LEFT to Green, RIGHT to Purple ))
                                ),
                                prismaColors = mapOf( LEFT to Orange, RIGHT to Green ))
                        ),
                        prismaColors = null
                    ),
                    GlowPathEntry(
                        position = Position(x = 3, y = 0),
                        parentPosition = null,
                        color = Purple,
                        children = listOf(
                            GlowPathEntry(
                                position = Position(x = 2, y = 0),
                                parentPosition = Position(x = 3, y = 0),
                                color = Purple,
                                children = listOf(
                                    GlowPathEntry(
                                        position = Position(x = 1, y = 0),
                                        parentPosition = Position(x = 2, y = 0),
                                        color = Orange,
                                        children = listOf(
                                            GlowPathEntry(
                                                position = Position(x = 0, y = 0),
                                                parentPosition = Position(x = 1, y = 0),
                                                color = Green,
                                                children = listOf(),
                                                prismaColors = null
                                            )
                                        ),
                                        prismaColors = mapOf( RIGHT to Orange, LEFT to Green ))
                                ),
                                prismaColors = mapOf( RIGHT to Purple, LEFT to Orange ))
                        ),
                        prismaColors = null
                    )
                )
            )
        )
    }
})
