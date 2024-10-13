package de.nielsfalk.laserhexagon

val Cell.futureConnectedNeighborPositions: List<Position>
    get() = futureConnectedNeighbors.map { (_, cell) -> cell.position }

data class GlowPath(
    val sources: List<GlowPathEntry> = listOf()
)

fun GlowPath.colors(x: Int, y: Int): Set<Color> =
    sources.mapNotNull { it.color(x, y) }.toSet()

fun GlowPath.prismaColors(x: Int, y: Int): Map<Direction, Set<Color>> {
    val result: List<Map<Direction, Color>> = sources.mapNotNull { it.prismaColors(x, y) }
    return Direction.entries.associateWith { direction ->
        result.mapNotNull { it[direction] }.toSet()
    }
}

operator fun GlowPath.get(position: Position): List<GlowPathEntry> =
    this[position.x, position.y]

private fun GlowPathEntry.color(x: Int, y: Int): Color? =
    if (position.x == x && position.y == y) {
        color
    } else {
        children.firstNotNullOfOrNull { it.color(x, y) }
    }

private fun GlowPathEntry.prismaColors(x: Int, y: Int): Map<Direction, Color>? =
    if (position.x == x && position.y == y) {
        prismaColors
    } else {
        children.firstNotNullOfOrNull { it.prismaColors(x, y) }
    }

fun GlowPath.colors(position: Position): Set<Color> =
    this.colors(position.x, position.y)

fun GlowPath.prismaColors(position: Position): Map<Direction, Set<Color>> =
    this.prismaColors(position.x, position.y)

operator fun GlowPath.get(x: Int, y: Int): List<GlowPathEntry> =
    sources.mapNotNull { it[x, y] }

private operator fun GlowPathEntry.get(x: Int, y: Int): GlowPathEntry? =
    if (position.x == x && position.y == y) {
        this
    } else {
        children.firstNotNullOfOrNull { it[x, y] }
    }

data class GlowPathEntry(
    val position: Position,
    val parentPosition: Position? = null,
    val color: Color,
    val children: List<GlowPathEntry> = listOf(),
    val prismaColors: Map<Direction, Color>? = null
)

fun Grid.initGlowPath(): Grid =
    copy(glowPath = glowPath.copy(sources = sources.map { (cell, color) ->
        GlowPathEntry(
            position = cell.position,
            parentPosition = null,
            color = color,
            children = listOf()
        )
    }))

fun Grid.removePrismaGlow(position: Position): Grid =
    if (this[position].prisma)
        copy(glowPath = glowPath.copy(sources = glowPath.sources.map { it.remove(position) }))
    else this

fun GlowPathEntry.remove(removeAt: Position): GlowPathEntry =
    copy(children = children.filter {
        it.position != removeAt
    }
        .map { it.remove(removeAt) })

fun Grid.removeDisconnectedFromPaths(): Grid =
    copy(glowPath = glowPath.copy(sources = glowPath.sources.map { it.removeDisconnected(this) }))

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
            val nextColor = prismaColors?.get(direction) ?: color
            if (cell.prisma) {
                var prismaDirection = direction.opposite
                var prismaColor = nextColor
                val prismaColors = mutableMapOf(prismaDirection to prismaColor)
                repeat(Direction.entries.size - 1) {
                    prismaDirection += 1
                    if (prismaDirection in cell.rotatedConnections) {
                        prismaColor = prismaColor.next
                        prismaColors[prismaDirection] = prismaColor
                    }
                }

                GlowPathEntry(
                    position = cell.position,
                    parentPosition = position,
                    color = nextColor,
                    prismaColors = prismaColors
                )
            } else {
                GlowPathEntry(
                    position = cell.position,
                    parentPosition = position,
                    color = nextColor
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