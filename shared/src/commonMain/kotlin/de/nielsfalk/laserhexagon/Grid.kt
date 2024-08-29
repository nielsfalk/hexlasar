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

data class Grid(
    val cells: List<Cell>,
    val x: Int = cells.maxOf { it.position.x } + 1,
    val y: Int = cells.maxOf { it.position.y } + 1,
    val glowPath: GlowPath = GlowPath(),
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

    val sources by lazy {
       COLOR.entries.map { color ->
           color to cells.filter { it.source == color }
       }.flatMap { (color, cells)->
           cells.map { it to color }
       }
    }
}

operator fun Grid.get(cellPosition: Position) = cells.first { it.position == cellPosition }

operator fun Grid.get(x: Int, y: Int): Cell = this[Position(x, y)]

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