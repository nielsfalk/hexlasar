package de.nielsfalk.laserhexagon

import de.nielsfalk.laserhexagon.Direction.*


data class Cell(
    val position: Position,
    val source: Color? = null,
    val endPoint: Set<Color> = emptySet(),
    val initialRotation: Int = 0,
    val rotatedParts: Int = 0,
    val rotations: Int = 0,
    val connections: Set<Direction> = setOf(),
    val locked: Boolean = false
) {
    private val rotatedConnections: Set<Direction> by lazy {
        connections.map { it.rotate(rotations) }.toSet()
    }

    private val futureRotatedConnections: Set<Direction> by lazy {
        connections.map { it.rotate(rotationWithParts.roundUp()) }.toSet()
    }
    lateinit var grid: Grid
    val neighborsPositions: Map<Direction, Position> by lazy {
        val neighbors = mapOf(
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
        )
        if (grid.connectBorders) {
            neighbors.mapValues { (_, position) ->
                position.copy(
                    x = (position.x + grid.x) % grid.x,
                    y = (position.y + grid.y) % grid.y
                )
            }
        } else {
            neighbors.filterValues {
                it.x >= 0
                        && it.y >= 0
                        && it.x < grid.x
                        && it.y < grid.y
            }
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

enum class Color {
    Red, Yellow, Blue;

    companion object
}

enum class Direction {
    LEFT, TOPLEFT, TOPRIGHT, RIGHT, BOTTOMRIGHT, BOTTOMLEFT;

    fun rotate(i: Int): Direction =
        Direction.entries[(ordinal + i) % Direction.entries.size]

    val opposite: Direction by lazy {
        rotate(Direction.entries.size / 2)
    }
}

fun Cell.toggleLock(): Cell =
    copy(locked = !locked)

val Cell.rotationWithParts: Float get() = rotations + rotatedParts / rotationSpeed.toFloat()
const val rotationSpeed = 200
