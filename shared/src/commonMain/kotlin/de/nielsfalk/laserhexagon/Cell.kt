package de.nielsfalk.laserhexagon

import de.nielsfalk.laserhexagon.Direction.BOTTOMLEFT
import de.nielsfalk.laserhexagon.Direction.BOTTOMRIGHT
import de.nielsfalk.laserhexagon.Direction.LEFT
import de.nielsfalk.laserhexagon.Direction.RIGHT
import de.nielsfalk.laserhexagon.Direction.TOPLEFT
import de.nielsfalk.laserhexagon.Direction.TOPRIGHT

data class Cell(
    val position: Position,
    val grid: Grid,
    val source: RGB? = null,
    val connections: MutableSet<Direction> = mutableSetOf()
) {
    val neighborsPositions: Map<Direction, Position> = mapOf(
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

    val neighbors: Map<Direction, Cell> = neighborsPositions.mapValues { (_,position)-> grid[position.x][position.y] }
}


data class Position(val x: Int, val y: Int)


class Grid(val x: Int = 10, val y: Int = 13) {
    val cells: List<List<Cell>> =
        (0 until x).map { x ->
            (0 until y).map { y ->
                Cell(Position(x, y), this)
            }
        }
}

operator fun Grid.get(x: Int): List<Cell> = cells[x]

enum class RGB { RED, GREEN, BLUE }
enum class Direction { LEFT, TOPLEFT, TOPRIGHT, RIGHT, BOTTOMRIGHT, BOTTOMLEFT }

val Int.odd: Boolean get() = this % 2 != 0
val Int.even: Boolean get() = this % 2 != 1

