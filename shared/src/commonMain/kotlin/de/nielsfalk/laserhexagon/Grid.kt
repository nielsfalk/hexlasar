package de.nielsfalk.laserhexagon

data class Grid(
    val cells: List<Cell>,
    val x: Int = cells.maxOf { it.position.x } + 1,
    val y: Int = cells.maxOf { it.position.y } + 1,
    val infiniteX: Boolean = false,
    val infiniteY: Boolean = false,
    val glowPath: GlowPath = GlowPath()
) {
    constructor(
        x: Int = 10,
        y: Int = 13,
        infiniteX: Boolean = false,
        infiniteY: Boolean = false
    ) : this(
        cells = (0 until x).map { cellX ->
            (0 until y).map { cellY ->
                Cell(Position(cellX, cellY))
            }
        }.flatten(),
        x = x,
        y = y,
        infiniteX = infiniteX,
        infiniteY = infiniteY
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
