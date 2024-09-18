package laserhexagon

import de.nielsfalk.laserhexagon.ControlledRandom
import de.nielsfalk.laserhexagon.randomExecution
import de.nielsfalk.laserhexagon.roundUp
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlin.test.assertEquals

class MiscTest : FreeSpec({
    mapOf(
        1.0f to 1,
        2.0001f to 2,
        2.002f to 3,
    ).forEach { (given, expected) ->
        "$given rounds up to $expected" {
            assertEquals(given.roundUp(), expected)
        }
    }

    "ControlledRandom" - {
        "10" {
            val givenInts = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            val random = ControlledRandom(
                givenInts
            )

            val result = (0 until givenInts.size).map { random.nextInt() }

            result shouldBe givenInts
        }
    }
    "random execution" {
        val givenInts = (0 until 100).toList()
        val random = ControlledRandom(
            givenInts
        )
        val resultRecorder = mutableMapOf<String, Int>()
        fun record(s: String) {
            val count = resultRecorder[s] ?: 0
            resultRecorder[s] = count + 1
        }

        repeat(givenInts.size) {
            randomExecution(random) {
                0 percentDo {
                    record("should not happen")
                }
                1 percentDo {
                    record("the one percent")
                }
                69 percentDo {
                    record("69")
                }
                30 percentDo {
                    record("last 30")
                }
                300 percentDo {
                    record("should not happen all 100 percent are defined")
                }
            }
        }

        assertEquals(
            resultRecorder, mapOf(
                "the one percent" to 1,
                "69" to 69,
                "last 30" to 30
            )
        )
    }
})
