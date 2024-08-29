package de.nielsfalk.laserhexagon

import de.nielsfalk.laserhexagon.Direction.BOTTOMLEFT
import de.nielsfalk.laserhexagon.Direction.BOTTOMRIGHT
import de.nielsfalk.laserhexagon.Direction.LEFT
import de.nielsfalk.laserhexagon.Direction.RIGHT
import de.nielsfalk.laserhexagon.Direction.TOPLEFT
import de.nielsfalk.laserhexagon.Direction.TOPRIGHT


data class Cell(
    val position: Position,
    val source: COLOR? = null,
    val endPoint: Set<COLOR> = emptySet(),
    val connected: Set<COLOR> = source?.let { setOf(it) } ?: setOf(),
    val initialRotation: Int = 0,
    val rotatedParts: Int = 0,
    val rotations: Int = 0,
    val connections: Set<Direction> = setOf(),
) {
    val rotatedConnections: Set<Direction> by lazy {
        connections.map { it.rotate(rotations) }.toSet()
    }

    val futureRotatedConnections: Set<Direction> by lazy {
        connections.map { it.rotate(rotationWithParts.roundUp()) }.toSet()
    }
    lateinit var grid: Grid
    val neighborsPositions: Map<Direction, Position> by lazy {
        mapOf(
            LEFT to Position(position.x - 1, position.y),
            TOPLEFT to
                    if (position.y.even) Position(position.x - 1, position.y - 1)
                    else Position(position.x, position.y - 1),
            TOPRIGHT to
                    if (position.y.even) Position(position.x, position.y - 1)
                    else Position(position.x + 1, position.y - 1),
            RIGHT to Position(position.x + 1, position.y),
            BOTTOMRIGHT to
                    if (position.y.even) Position(position.x, position.y + 1)
                    else Position(position.x + 1, position.y + 1),
            BOTTOMLEFT to
                    if (position.y.even) Position(position.x - 1, position.y + 1)
                    else Position(position.x, position.y + 1)
        ).filterValues {
            it.x >= 0
                    && it.y >= 0
                    && it.x < grid.x
                    && it.y < grid.y
        }

    }
    val neighbors: Map<Direction, Cell>
        get() = neighborsPositions.mapValues { (_, position) -> grid[position] }
    val connectedNeighbors: Map<Direction, Cell>
        get() = neighbors.filter { (direction, cell) ->
            direction in rotatedConnections && direction.opposite in cell.rotatedConnections
        }
    val futureConnectedNeighbors: Map<Direction, Cell>
        get() = neighbors.filter { (direction, cell) ->
            direction in futureRotatedConnections && direction.opposite in cell.futureRotatedConnections
        }
    val neighborsDirections: Set<Direction> by lazy { neighborsPositions.keys }
}

data class Position(val x: Int, val y: Int)

enum class COLOR { RED, YELLOW, BLUE }
enum class Direction {
    LEFT, TOPLEFT, TOPRIGHT, RIGHT, BOTTOMRIGHT, BOTTOMLEFT;

    fun rotate(i: Int): Direction =
        Direction.entries[(ordinal + i) % Direction.entries.size]

    val opposite: Direction by lazy {
        rotate(Direction.entries.size / 2)
    }
}

val Cell.rotationWithParts: Float get() = rotations + rotatedParts / rotationSpeed.toFloat()
const val rotationSpeed = 200
