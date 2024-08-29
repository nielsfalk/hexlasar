package de.nielsfalk.laserhexagon

val Cell.connectedNeighborPositions: List<Position>
    get() = connectedNeighbors.map { (_, cell) -> cell.position }
val Cell.futureConnectedNeighborPositions: List<Position>
    get() = futureConnectedNeighbors.map { (_, cell) -> cell.position }

data class GlowPath(
    val sources: List<GlowPathEntry> = listOf()
)

operator fun GlowPath.get(x: Int, y: Int): List<COLOR> =
    sources.flatMap { it[x, y] }

private operator fun GlowPathEntry.get(x: Int, y: Int): Set<COLOR> =
    if (position.x == x && position.y == y) {
        setOf(color)
    } else {
        children.flatMap { it[x, y] }.toSet()
    }

data class GlowPathEntry(
    val position: Position,
    val parentPostition: Position? = null,
    val color: COLOR,
    val children: List<GlowPathEntry> = listOf(),
)

fun Grid.initGlowPath(): Grid =
    copy(glowPath = glowPath.copy(sources = sources.map { (cell, color) ->
        GlowPathEntry(
            position = cell.position,
            parentPostition = null,
            color = color,
            children = listOf()
        )
    }))

fun Grid.removeDisconnectedFromPaths(): Grid =
    copy(glowPath = glowPath.copy(glowPath.sources.map { it.removeDisconnected(this) }))

fun GlowPathEntry.removeDisconnected(grid: Grid): GlowPathEntry =
    copy(children = children.filter {
        val cell = grid[position]
        it.position in cell.futureConnectedNeighborPositions
    }
        .map { it.removeDisconnected(grid) })

fun Grid.followPath(): Grid =
    copy(glowPath = glowPath.copy(glowPath.sources.map { it.follow(this) }))

fun GlowPathEntry.follow(grid: Grid, root: GlowPathEntry = this): GlowPathEntry {
    val cell = grid[position]
    val newChildren = cell.connectedNeighborPositions.filter {
        it !in root
    }
        .map {
            GlowPathEntry(
                position = it,
                parentPostition = position,
                color = color
            )
        }

    return copy(children = children.map {
        it.follow(
            grid = grid,
            root = root
        )
    } + newChildren)
}

private operator fun GlowPathEntry.contains(position: Position): Boolean =
    this.position == position || children.any { it.contains(position) }

internal fun Grid.followPathComplete(): Grid {
    var result = followPath()
    var lastResult: Grid? = null
    while (result != lastResult) {
        lastResult = result
        result = result.followPath()
    }
    return result
}
