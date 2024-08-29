package de.nielsfalk.laserhexagon

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class MiscTest : FreeSpec({
    mapOf(
        1.0f to 1,
        2.001f to 2,
        2.01f to 3,
    ).forEach { (given, expected) ->
        "$given rounds up to $expected" {
            given.roundUp() shouldBe expected
        }
    }
})