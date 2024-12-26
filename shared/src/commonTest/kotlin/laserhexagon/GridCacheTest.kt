package laserhexagon

import de.nielsfalk.laserhexagon.GridCache.allACacheKeys
import de.nielsfalk.laserhexagon.GridCache.cache
import de.nielsfalk.laserhexagon.GridCache.get
import de.nielsfalk.laserhexagon.LevelType.EASY
import io.kotest.core.spec.style.FreeSpec
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GridCacheTest : FreeSpec({
    "all cache keys have right size" {
        assertEquals(22, allACacheKeys.size)
    }
    "get" - {
        listOf("empty", "filled").forEach {
            "with $it cache" {
                val grid = testScope.get(EASY, true)

                assertNotNull(grid)
                assertCacheIsFilled()
            }
        }
    }
})

private suspend fun assertCacheIsFilled() {
    waitFor {
        assertEquals(setOf(3), cache.map { it.value.size }.toSet())
        assertEquals(allACacheKeys, cache.keys)
    }
}

private suspend fun waitFor(
    timeoutMillis: Long = 100,
    delayMillis: Long = 1,
    function: () -> Unit
) {
    val tryUntil = epochMilliseconds() + timeoutMillis
    @Suppress("ControlFlowWithEmptyBody")
    while (runCatching(function)
            .getOrElse { e ->
                if (epochMilliseconds() < tryUntil) {
                    delay(delayMillis)
                    null
                } else {
                    throw e
                }
            } != null
    ) {
    }
}

private fun epochMilliseconds() = Clock.System.now().toEpochMilliseconds()
