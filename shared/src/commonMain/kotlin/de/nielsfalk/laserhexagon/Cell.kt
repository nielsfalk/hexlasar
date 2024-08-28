package de.nielsfalk.laserhexagon

import de.nielsfalk.laserhexagon.COLOR.BLUE
import de.nielsfalk.laserhexagon.COLOR.RED
import de.nielsfalk.laserhexagon.COLOR.YELLOW
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
    val rotatedConnections:Set<Direction> by lazy {
        connections.map { it.rotate(rotations)}.toSet()
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
    val neighborsDirections: Set<Direction> by lazy { neighborsPositions.keys }
}


data class Position(val x: Int, val y: Int)


data class Grid(
    val cells: List<Cell>,
    val x: Int = cells.maxOf { it.position.x } + 1,
    val y: Int = cells.maxOf { it.position.y } + 1,
) {
    constructor(x: Int = 10, y: Int = 13) : this(
        (0 until x).map { x ->
            (0 until y).map { y ->
                Cell(Position(x, y))
            }
        }.flatten(),
        x,
        y
    )

    init {
        cells.forEach { it.grid = this }
    }

    fun update(vararg modifiedCells: Cell): Grid {
        val modifiedPositions = modifiedCells.map { it.position }
        return copy(cells = cells.filter { it.position !in modifiedPositions }
                + modifiedCells)
    }
}


operator fun Grid.get(cellPosition: Position) = cells.first { it.position == cellPosition }

operator fun Grid.get(x: Int, y: Int): Cell = this[Position(x, y)]

enum class COLOR { RED, YELLOW, BLUE }
enum class Direction {
    LEFT, TOPLEFT, TOPRIGHT, RIGHT, BOTTOMRIGHT, BOTTOMLEFT;

    fun rotate(i: Int): Direction =
        Direction.entries[(ordinal + i) % Direction.entries.size]


    val opposite: Direction by lazy {
        rotate(Direction.entries.size/2)
    }
}


val Int.odd: Boolean get() = this % 2 != 0
val Int.even: Boolean get() = this % 2 != 1
val testGrid = Grid(5, 6).run {
    val cellIterator = cells.iterator()
    update(
        cellIterator.next().copy(
            connections = setOf(LEFT),
            source = RED,
            connected = setOf(RED)
        ),
        cellIterator.next().copy(
            connections = setOf(TOPLEFT),
            source = BLUE,
            connected = setOf(BLUE)
        ),
        cellIterator.next().copy(
            connections = setOf(TOPRIGHT),
            source = YELLOW,
            connected = setOf(YELLOW)

        ),
        cellIterator.next().copy(
            connections = setOf(RIGHT),
            endPoint = setOf(RED)
        ),
        cellIterator.next().copy(
            connections = setOf(BOTTOMRIGHT),
            endPoint = setOf(RED, YELLOW)
        ),
        cellIterator.next().copy(connections = setOf(BOTTOMLEFT)),
        cellIterator.next().copy(
            connections = Direction.entries.toSet(),
            connected = setOf(RED, BLUE)
        ),
        cellIterator.next().copy(
            connections = Direction.entries.toSet(),
            connected = setOf(YELLOW, BLUE)
        ),
        cellIterator.next().copy(connections = Direction.entries.toSet())
    )

}

