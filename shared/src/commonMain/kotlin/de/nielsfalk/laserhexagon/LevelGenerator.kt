package de.nielsfalk.laserhexagon

import kotlin.math.min
import kotlin.random.Random

private val Cell.freeNeighbors: Map<Direction, Cell>
    get() = neighbors.filter { (_, cell) -> cell.source == null && cell.connections.isEmpty() }

private val Grid.emptyCells: List<Cell>
    get() = cells.filter { it.source == null && it.connections.isEmpty() }

class LevelGenerator(
    val x: Int = 3,
    val y: Int = 6,
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
        scramble()
        return grid.copy(glowPath = GlowPath()).initGlowPath()
    }

    private fun scramble() {
        grid = grid.update(
            grid.cells.filter { it.connections.isNotEmpty() && it.connections.size != it.neighbors.size }
                .map { val initialRotation = random.nextInt(Direction.entries.size)
                    it.copy(
                    initialRotation = initialRotation,
                    rotations = initialRotation
                ) }
        )
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
                    1 percentDo { 6 }
                    2 percentDo { 5 }
                    25 percentDo { 4 }
                    25 percentDo { 3 }
                    25 percentDo { 2 }
                    elseDo { 1 }
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
                    3 percentDo { 5 }
                    25 percentDo { 4 }
                    25 percentDo { 3 }
                    25 percentDo { 2 }
                    elseDo { 1 }
                })
                grid = grid.connect(endpoint, freeNeighbors.take(random, connectionCount))
            }
    }
}

private fun Grid.connect(source: Cell, neighbors: Map<Direction, Cell>): Grid =
    update(
        neighbors.map { (direction, cell) -> cell.copy(connections = cell.connections + direction.opposite) } +
                source.let { it.copy(connections = it.connections + neighbors.keys) }
    )

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
