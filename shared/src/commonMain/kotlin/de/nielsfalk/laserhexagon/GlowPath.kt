package de.nielsfalk.laserhexagon

val Cell.futureConnectedNeighborPositions: List<Position>
    get() = futureConnectedNeighbors.map { (_, cell) -> cell.position }

data class GlowPath(
    val sources: List<GlowPathEntry> = listOf()
)

fun GlowPath.colors(x: Int, y: Int): Set<Color> =
    sources.mapNotNull { it.color(x, y) }.toSet()

operator fun GlowPath.get(position: Position): List<GlowPathEntry> =
    this[position.x, position.y]

private fun GlowPathEntry.color(x: Int, y: Int): Color? =
    if (position.x == x && position.y == y) {
        color
    } else {
        children.mapNotNull { it.color(x, y) }.firstOrNull()
    }

fun GlowPath.colors(position: Position): Set<Color> =
    this.colors(position.x, position.y)

operator fun GlowPath.get(x: Int, y: Int): List<GlowPathEntry> =
    sources.mapNotNull { it[x, y] }

private operator fun GlowPathEntry.get(x: Int, y: Int): GlowPathEntry? =
    if (position.x == x && position.y == y) {
        this
    } else {
        children.mapNotNull { it[x, y] }.firstOrNull()
    }

data class GlowPathEntry(
    val position: Position,
    val parentPostition: Position? = null,
    val color: Color,
    val prismaFrom: Direction? = null,
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
    copy(glowPath = glowPath.copy(sources = glowPath.sources.map { it.follow(this) }))

fun GlowPathEntry.follow(grid: Grid, root: GlowPathEntry = this): GlowPathEntry {
    val cell = grid[position]
    val newChildren = cell.connectedNeighbors.filter { (_, cell) ->
        cell.position !in root
    }
        .map { (direction, cell) ->
            if (cell.prisma) {
                GlowPathEntry(
                    position = cell.position,
                    parentPostition = position,
                    color = color.next,
                    prismaFrom= direction.opposite
                )
            } else {
                GlowPathEntry(
                    position = cell.position,
                    parentPostition = position,
                    color = color
                )
            }
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

const val glowSpeed = 200