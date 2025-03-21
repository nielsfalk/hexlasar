package de.nielsfalk.laserhexagon

import kotlin.math.min
import kotlin.random.Random

val Set<Direction>.scrambleAmount: Int
    get() = when {
        isEmpty() || size == Direction.entries.size -> 0
        size == 2 && contains(first().opposite) -> {
            Direction.entries.size / 2
        }

        size == 3 && contains(first() + 2) && contains(first() + 4) -> {
            Direction.entries.size / 3
        }

        size == 4 && contains(first().opposite)->{
            val withoutFirstAndItsOpposite = this - listOf(first(), first().opposite)
            if (withoutFirstAndItsOpposite.contains(withoutFirstAndItsOpposite.first().opposite))
                Direction.entries.size / 2
            else null
        }

        else -> null
    } ?: Direction.entries.size

private val Cell.freeNeighbors: Map<Direction, Cell>
    get() = neighbors.filter { (_, cell) -> cell.source == null && cell.connections.isEmpty() }

internal val Grid.emptyCells: List<Cell>
    get() = cells.filter { it.source == null && it.connections.isEmpty() }


class GridGenerator(
    val random: Random = Random.Default,
    val levelType: LevelType = LevelType.entries.first(),
    val levelProperties: LevelProperties = levelType.levelProperties.random(random)
) {
    var grid = newGrid()

    private fun newGrid() = Grid(
        levelProperties.x,
        levelProperties.y,
        infiniteX = levelType.infiniteX,
        infiniteY = levelType.infiniteY
    )


    fun generate(): Grid {
        while (true) {
            levelProperties.run {
                repeat(sourceCount) {
                    generateSource()
                    generateSourceConnections()
                }
                repeat(x * y) { generateNextPart() }
                removeUnconnectedSources()
                repeat(sourceCount - 1) { generateNextPart() }
                repeat(random.nextInt(3)) {
                    connectColors()
                }
                if (maxPrismaCount != 0) {
                    repeat(random.nextInt(maxPrismaCount) + 1) {
                        addPrisma()
                    }
                }
                setEndPointColors()
                scramble()
                if (grid.cells.sumOf { it.initialRotation } > 0) {
                    grid = grid.copy(glowPath = GlowPath()).initGlowPath()
                    return grid
                }else{
                    grid = newGrid()
                }
            }
        }
    }

    private fun addPrisma() {
        glow()
        grid.cells.filter {
            it.source == null && it.connections.size > 1
        }
            .randomOrNull(random)
            ?.let {
                grid = grid.update(
                    it.copy(
                        prisma = true,
                    )
                )
            }
    }

    private fun scramble() {
        grid = grid.update(
            grid.cells.filter {
                it.connections.isNotEmpty() &&
                        (levelProperties.rotateObvious || it.connections.size != it.neighbors.size) &&
                        it.connections.scrambleAmount > 0
            }
                .map {
                    val initialRotation = random.nextInt(it.connections.scrambleAmount)
                    it.copy(
                        initialRotation = initialRotation,
                        rotations = initialRotation
                    )
                }
        )
    }

    private fun setEndPointColors() {
        glow()
        grid = grid.update(
            grid.cells.filter { it.source == null && it.connections.size == 1 }
                .map { it.copy(endPoint = grid.glowPath.colors(it.position)) }
        )
    }

    private fun connectColors() {
        glow()
        val cell = grid.cells.filter { cell ->
            cell.source == null &&
                    cell.getNeighborWithOtherColor().isNotEmpty()
        }.randomOrNull(random)
        cell?.let {
            grid = grid.connect(cell, it.getNeighborWithOtherColor().take(random, 1))
        }
    }

    private fun Cell.getNeighborWithOtherColor(): Map<Direction, Cell> {
        val colors = this@GridGenerator.grid.glowPath.colors(position)
        return if (colors.size == 1) {
            neighbors.filter { (_, neighbor) ->
                neighbor.source == null && colors.first() !in this@GridGenerator.grid.glowPath.colors(neighbor.position)
            }
        } else mapOf()
    }

    private fun glow() {
        grid = grid.copy(glowPath = GlowPath()).initGlowPath().followPathComplete()
    }

    private fun removeUnconnectedSources() {
        grid.update(
            grid.cells.filter { it.source != null && it.connections.isEmpty() }
                .map { it.copy(source = null) }
        )
    }

    private fun generateSource() {
        grid.emptyCells.filter {
            it.neighbors.any { (_, neighbor) -> neighbor.connections.isEmpty() }
        }.randomOrNull(random)?.let {
            grid = grid.update(
                it.copy(
                    source = Color.random(random),
                )
            )
        }
    }

    private fun generateSourceConnections() {
        val unconnectedSource = grid.cells.filter { it.source != null && it.connections.isEmpty() }
        if (unconnectedSource.isNotEmpty()) {
            val source = unconnectedSource.random(random)
            val freeNeighbors = source.freeNeighbors
            val connectionCount: Int = min(freeNeighbors.size,
                randomExecution(random) {
                    1 percentDo { 6 }
                    2 percentDo { 5 }
                    25 percentDo { 4 }
                    25 percentDo { 3 }
                    25 percentDo { 2 }
                    elseDo { 1 }
                }
            )
            grid = grid.connect(source, freeNeighbors.take(random, connectionCount))
        }
    }

    private fun generateNextPart() {
        grid.cells.filter { it.connections.size == 1 && it.source == null && it.freeNeighbors.isNotEmpty() }
            .randomOrNull(random)
            ?.let { endpoint ->
                val freeNeighbors = endpoint.freeNeighbors
                val connectionCount: Int = min(freeNeighbors.size, randomExecution(random) {
                    3 percentDo { 5 }
                    25 percentDo { 4 }
                    25 percentDo { 3 }
                    25 percentDo { 2 }
                    elseDo { 1 }
                })
                grid = grid.connect(endpoint, freeNeighbors.take(random, connectionCount))
            }
    }
}

private fun Grid.connect(source: Cell, neighbors: Map<Direction, Cell>): Grid =
    update(
        neighbors.map { (direction, cell) -> cell.copy(connections = cell.connections + direction.opposite) } +
                source.let { it.copy(connections = it.connections + neighbors.keys) }
    )

private fun Map<Direction, Cell>.take(random: Random, connectionCount: Int): Map<Direction, Cell> =
    if (size == connectionCount)
        this
    else {
        val result = mutableMapOf<Direction, Cell>()
        while (result.size < connectionCount) {
            val (direction, cell) = this@take.entries.randomOrNull(random)
                ?: break
            result[direction] = cell
        }
        result
    }

private fun Color.Companion.random(random: Random): Color =
    Color.entries[random.nextInt(Color.entries.size)]
