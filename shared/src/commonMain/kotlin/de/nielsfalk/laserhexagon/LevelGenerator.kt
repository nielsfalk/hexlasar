package de.nielsfalk.laserhexagon

import kotlin.math.max
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
    fun generate(): Grid {
        var grid = Grid(x, y)

        repeat(sourceCount) {
            grid = grid.update(
                grid.emptyCells.random(random).copy(
                    source = COLOR.random(random),
                )
            )
        }
        repeat(sourceCount) {
            val unconnectedSource = grid.cells.filter { it.source != null && it.connections.isEmpty() }
            if (unconnectedSource.isNotEmpty()) {
                val source = unconnectedSource.random(random)
                val freeNeighbors = source.freeNeighbors
                val connectionCount: Int = max(freeNeighbors.size,
                    randomExecution(random) {
                        1 `percent do` { 6 }
                        2 `percent do` { 5 }
                        25 `percent do` { 4 }
                        25 `percent do` { 3 }
                        25 `percent do` { 2 }
                        `else do` { 1 }
                    }
                )
                grid.connect(source, freeNeighbors.take(random, connectionCount))
            }
        }
        return grid
    }
}

private fun Grid.connect(source: Cell, neighbors: Map<Direction, Cell>) {
    val cells = mutableListOf(source.copy(connections = neighbors.keys))
    cells.addAll(neighbors.map { (direction, cell) -> cell.copy(connections = setOf(direction.opposite)) })
    update(
        *cells.toTypedArray()
    )
}

private fun Map<Direction, Cell>.take(random: Random, connectionCount: Int): Map<Direction, Cell> =
    if (size <= connectionCount) this
    else
        mutableMapOf<Direction, Cell>().apply {
            while (size <= connectionCount) {
                val (direction, cell) = this@take.entries.random(random)
                put(direction, cell)
            }
        }

private fun COLOR.Companion.random(random: Random): COLOR =
    COLOR.entries[random.nextInt(COLOR.entries.size)]
