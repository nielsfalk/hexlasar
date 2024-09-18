package de.nielsfalk.games.hextris

import io.kotest.core.spec.style.FreeSpec
import kotlin.test.assertEquals

class PowerAssertTest : FreeSpec({
    "ComposeApp Power-assert with Kotest" {
        val hello = "Hello"
        val world = "world!"
        assertEquals(hello.length, world.substring(1, 4).length)
    }
})