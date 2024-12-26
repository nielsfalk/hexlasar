package de.nielsfalk.laserhexagon

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


object GridCache {
    val allACacheKeys = LevelType.entries.flatMap {
        listOf(
            CacheKey(it, true),
            CacheKey(it, false)
        )
    }.toSet()

    internal val cache: Map<CacheKey, MutableList<Grid>> = allACacheKeys.associateWith { mutableListOf() }

    fun CoroutineScope.get(levelType: LevelType, toggleXY: Boolean): Grid {
        val cacheKey = CacheKey(levelType, toggleXY)
        return getOrGenerate(cacheKey)
            .also { fillCache(cacheKey) }
    }

    private fun CoroutineScope.fillCache(prefer: CacheKey) {
        allACacheKeys
            .sortedBy {
                when {
                    it.levelType == prefer.levelType && it.toggleXY == prefer.toggleXY -> 0
                    it.levelType == prefer.levelType -> 1
                    it.toggleXY == prefer.toggleXY -> 2
                    else -> 10
                }
            }
            .forEach { cacheKey ->
                launch {
                    while (cache[cacheKey]!!.size < 3) {
                        cache[cacheKey]!!.add(generate(cacheKey))
                    }
                }
            }
    }

    private fun getOrGenerate(
        cacheKey: CacheKey
    ): Grid {
        val grids = cache[cacheKey]!!
        return if (grids.isEmpty()) generate(cacheKey)
        else grids.removeFirst()
    }
}

data class CacheKey(
    val levelType: LevelType = LevelType.entries.first(),
    val toggleXY: Boolean = false
)

private fun generate(
    cacheKey: CacheKey
) =
    GridGenerator(
        levelType = cacheKey.levelType,
        levelProperties = cacheKey.levelType.levelProperties.random().let {
            if (cacheKey.toggleXY)
                it.copy(x = it.y, y = it.x)
            else it
        }
    )
        .generate()
        .initGlowPath()
