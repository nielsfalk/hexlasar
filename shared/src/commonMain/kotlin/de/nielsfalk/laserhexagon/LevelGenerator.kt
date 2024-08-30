package de.nielsfalk.laserhexagon

import kotlin.math.min
import kotlin.random.Random

private val Cell.freeNeighbors: Map<Direction, Cell>
    get() = neighbors.filter { (_, cell) -> cell.source == null && cell.connections.isEmpty() }

private val Grid.emptyCells: List<Cell>
    get() = cells.filter { it.source == null && it.connections.isEmpty() }

class LevelGenerator(
    val x: Int = 4,
    val y: Int = 5,
    val sourceCount: Int = 3,
    val random: Random = Random.Default
) {
    var grid = Grid(x, y)

    fun generate(): Grid {
        repeat(sourceCount) { generateSource() }
        repeat(sourceCount) { generateSourceConnections() }
        repeat(x * y) { generateNextPart() }
        removeUnconnectedSources()
        repeat(sourceCount - 1) { generateNextPart() }
        repeat(random.nextInt(3)) {
            connectColors()
        }
        setEndPointColors()
        return grid.copy(glowPath = GlowPath()).initGlowPath()
    }

    private fun setEndPointColors() {
        glow()
        grid = grid.update(
            grid.cells.filter { it.source == null && it.connections.size == 1 }
                .map { it.copy(endPoint = grid.glowPath[it.position]) }
        )
    }

    private fun connectColors() {
        glow()
        val cell = grid.cells.filter { it.source == null }.random()
        val colors = grid.glowPath[cell.position]
        if (colors.size == 1) {
            val neighborWithOtherColor = cell.neighbors.filter { (_, neighbor) ->
                neighbor.source == null && colors.first() !in grid.glowPath[neighbor.position]
            }
            grid = grid.connect(cell, neighborWithOtherColor.take(random, 1))
        }
    }

    private fun glow() {
        grid = grid.copy(glowPath = GlowPath()).initGlowPath().followPathComplete()
    }

    private fun removeUnconnectedSources() {
        grid.update(
            grid.cells.filter { it.source != null && it.connections.isEmpty() }
                .map { it.copy(source = null) }
        )
    }

    private fun generateSourceConnections() {
        val unconnectedSource = grid.cells.filter { it.source != null && it.connections.isEmpty() }
        if (unconnectedSource.isNotEmpty()) {
            val source = unconnectedSource.random(random)
            val freeNeighbors = source.freeNeighbors
            val connectionCount: Int = min(freeNeighbors.size,
                randomExecution(random) {
                    1 `percent do` { 6 }
                    2 `percent do` { 5 }
                    25 `percent do` { 4 }
                    25 `percent do` { 3 }
                    25 `percent do` { 2 }
                    `else do` { 1 }
                }
            )
            grid = grid.connect(source, freeNeighbors.take(random, connectionCount))
        }
    }

    private fun generateSource() {
        grid = grid.update(
            grid.emptyCells.random(random).copy(
                source = COLOR.random(random),
            )
        )
    }

    private fun generateNextPart() {
        grid.cells.filter { it.connections.size == 1 && it.source == null && it.freeNeighbors.isNotEmpty() }
            .randomOrNull(random)
            ?.let { endpoint ->
                val freeNeighbors = endpoint.freeNeighbors
                val connectionCount: Int = min(freeNeighbors.size, randomExecution(random) {
                    3 `percent do` { 5 }
                    25 `percent do` { 4 }
                    25 `percent do` { 3 }
                    25 `percent do` { 2 }
                    `else do` { 1 }
                })
                grid = grid.connect(endpoint, freeNeighbors.take(random, connectionCount))
            }
    }
}

private fun Grid.connect(source: Cell, neighbors: Map<Direction, Cell>): Grid {
    return update(
        neighbors.map { (direction, cell) -> cell.copy(connections = setOf(direction.opposite)) } +
                source.let { it.copy(connections = neighbors.keys + it.connections) }
    )
}

private fun Map<Direction, Cell>.take(random: Random, connectionCount: Int): Map<Direction, Cell> =
    if (size == connectionCount)
        this
    else {
        val result = mutableMapOf<Direction, Cell>()
        while (result.size < connectionCount) {
            val (direction, cell) = this@take.entries.randomOrNull(random)
                ?: break
            result[direction] = cell
        }
        result
    }

private fun COLOR.Companion.random(random: Random): COLOR =
    COLOR.entries[random.nextInt(COLOR.entries.size)]
