package de.nielsfalk.laserhexagon

import de.nielsfalk.laserhexagon.Color.*
import de.nielsfalk.laserhexagon.Direction.*

data class Grid(
    val cells: List<Cell>,
    val x: Int = cells.maxOf { it.position.x } + 1,
    val y: Int = cells.maxOf { it.position.y } + 1,
    val connectBorders: Boolean = false,
    val glowPath: GlowPath = GlowPath()
) {
    constructor(x: Int = 10, y: Int = 13, connectBorders: Boolean = false) : this(
        (0 until x).map { x ->
            (0 until y).map { y ->
                Cell(Position(x, y))
            }
        }.flatten(),
        x,
        y,
        connectBorders
    )

    init {
        cells.forEach { it.grid = this }
    }

    fun update(vararg modifiedCells: Cell): Grid {
        val modifiedPositions = modifiedCells.map { it.position }
        return copy(cells = cells.filter { it.position !in modifiedPositions }
                + modifiedCells)
    }

    fun update(modifiedCells: List<Cell>): Grid =
        update(*modifiedCells.toTypedArray())

    val sources by lazy {
        Color.entries.map { color ->
            color to cells.filter { it.source == color }
        }.flatMap { (color, cells) ->
            cells.map { it to color }
        }
    }

    val endpoints by lazy {
        cells.filter { it.endPoint.isNotEmpty() }
    }

    val solved: Boolean by lazy {
        endpoints.all {
            glowPath.colors(it.position).containsAll(it.endPoint)
        }
    }

    val solvedAndLocked by lazy {
        cells.all { it.locked } && solved
    }

    val started: Boolean by lazy {
        cells.any { it.rotations != it.initialRotation }
    }
}

operator fun Grid.get(cellPosition: Position) = cells.first { it.position == cellPosition }

operator fun Grid.get(x: Int, y: Int): Cell = this[Position(x, y)]

fun Grid.reset(): Grid =
    initGlowPath()
        .update(*cells.map {
            it.copy(
                rotations = it.initialRotation,
                rotatedParts = 0,
                locked = false
            )
        }.toTypedArray())

fun Grid.lockAllCells(): Grid =
    update(cells.map { it.copy(locked = true) })

val testGrid = Grid(5, 6).run {
    val cellIterator = cells.iterator()
    update(
        cellIterator.next().copy(
            connections = setOf(LEFT),
            source = Red
        ),
        cellIterator.next().copy(
            connections = setOf(TOPLEFT),
            source = Blue
        ),
        cellIterator.next().copy(
            connections = setOf(TOPRIGHT),
            source = Yellow
        ),
        cellIterator.next().copy(
            connections = setOf(RIGHT),
            endPoint = setOf(Red)
        ),
        cellIterator.next().copy(
            connections = setOf(BOTTOMRIGHT),
            endPoint = setOf(Red, Yellow)
        ),
        cellIterator.next().copy(connections = setOf(BOTTOMLEFT)),
        cellIterator.next().copy(connections = Direction.entries.toSet()),
        cellIterator.next().copy(connections = Direction.entries.toSet()),
        cellIterator.next().copy(connections = Direction.entries.toSet()),
        cellIterator.next().copy(connections = Direction.entries.toSet()),
        cellIterator.next().copy(connections = Direction.entries.toSet()),
        cellIterator.next().copy(connections = setOf(LEFT, TOPLEFT)),
        cellIterator.next().copy(connections = setOf(LEFT, TOPRIGHT)),
        cellIterator.next().copy(connections = setOf(LEFT, RIGHT)),
        cellIterator.next().copy(connections = setOf(LEFT, BOTTOMLEFT)),
        cellIterator.next().copy(connections = setOf(LEFT, BOTTOMRIGHT)),
        cellIterator.next().copy(connections = Direction.entries.toSet())
    )
}