package de.nielsfalk.laserhexagon

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