package de.nielsfalk.laserhexagon

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class LevelGeneratorTest:FreeSpec({
    "Generate small"{
        val random = ControlledRandom(
            0, //first Source
            0, //its color
            0, //use source
            60, //percentage

        )
        val levelGenerator = LevelGenerator(
            x = 3,
            y = 3,
            sourceCount = 1,
            random = random
        )

        val grid = levelGenerator.generate()

        grid.sources.size shouldBe 1
    }
})