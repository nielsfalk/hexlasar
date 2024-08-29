package de.nielsfalk.laserhexagon

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class LevelGeneratorTest:FreeSpec({
    "Generate small"{
        val random = ControlledRandom(
            0, //first Source
            0, //its color
            5, //second Source
            1, //its color

        )
        val levelGenerator = LevelGenerator(
            x = 3,
            y = 3,
            sourceCount = 2,
            random = random
        )

        val grid = levelGenerator.generate()

        grid.sources.size shouldBe 2
    }
})