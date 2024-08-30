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
            0,0,0,0,0,0,0,0,0,
            //scramble
            1,1,1,1,1,1,1,1,1
        )
        val levelGenerator = LevelGenerator(
            levelProperties = LevelProperties(
                x = 3,
                y = 3,
                sourceCount = 1
            ),
            random = random
        )

        val grid = levelGenerator.generate()

        grid.sources.size shouldBe 1
    }
})