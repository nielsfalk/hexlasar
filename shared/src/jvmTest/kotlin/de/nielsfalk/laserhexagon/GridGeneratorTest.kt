package de.nielsfalk.laserhexagon

import de.nielsfalk.laserhexagon.Direction.*
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class GridGeneratorTest : FreeSpec({
    "scrambleAmount" - {
        listOf(
            setOf<Direction>() to 0,
            Direction.entries.toSet() to 0,
            setOf(LEFT) to 6,
            setOf(LEFT, TOPLEFT) to 6,
            setOf(LEFT, RIGHT) to 3,
            setOf(TOPLEFT, BOTTOMRIGHT) to 3,
            setOf(TOPRIGHT, BOTTOMLEFT) to 3,
            setOf(LEFT, TOPLEFT, BOTTOMRIGHT) to 6,
            setOf(LEFT, TOPRIGHT, BOTTOMRIGHT) to 2,
            setOf(TOPLEFT, RIGHT, BOTTOMLEFT) to 2,
            setOf(LEFT, TOPLEFT, RIGHT, BOTTOMRIGHT) to 3
        ).forEach { (connections, expected) ->
            "with $connections expect $expected" {
                connections.scrambleAmount shouldBe expected
            }
        }
    }
    "Generate small" {
        val random = ControlledRandom(
            0, //first Source
            0, //its color
            0, //use source
            0, //percentage
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            //scramble
            1, 1, 1, 1, 1, 1, 1, 1, 1
        )
        val gridGenerator = GridGenerator(
            levelProperties = LevelProperties(
                x = 3,
                y = 3,
                sourceCount = 1,
                maxPrismaCount = 0
            ),
            random = random
        )

        val grid = gridGenerator.generate().followPathComplete()

        grid.sources shouldHaveSize 1
        grid.endpoints shouldHaveSize 4
        grid.emptyCells shouldHaveSize 0
    }
})