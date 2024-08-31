package de.nielsfalk.laserhexagon

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import de.nielsfalk.laserhexagon.Direction.*
import de.nielsfalk.laserhexagon.GameEvent.LeakCellPositions
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun GameCanvas(
    modifier: Modifier,
    grid: Grid,
    onEvent: (GameEvent) -> Unit,
) {
    Canvas(
        modifier = modifier
    ) {
        when {
            size.run { width > height } && grid.run { x < y } && !grid.started -> {
                onEvent(GameEvent.ToggleXYWithLevelGeneration(true))
            }

            size.run { width < height } && grid.run { x > y } && !grid.started-> {
                onEvent(GameEvent.ToggleXYWithLevelGeneration(false))
            }

            else -> {
                drawRect(color = Color.Black, size = size)

                drawGame(grid)

                onEvent(LeakCellPositions(
                    mutableMapOf<Offset, Position>().apply {
                        this@Canvas.onAllCells(grid, this@Canvas.size.width) {
                            put(cellCenterOffset, cell.position)
                        }
                    }
                ))
            }
        }
    }
}

private fun DrawScope.drawGame(grid: Grid) {
    onAllCells(grid, size.width) {
        drawCellBorder(partsPixel)
        drawCellLock(partsPixel)

        drawConnections(partsPixel, grid.glowPath)
        drawEndpoint(partsPixel, grid.glowPath)
        drawMiddlePoint(partsPixel, grid.glowPath)
        drawSource(partsPixel)
    }
}

private fun CellDrawScope.drawMiddlePoint(
    partsPixel: Float,
    glowPath: GlowPath
) {
    onLayer(1){
        drawCircle(
            color = Color.White,
            radius = partsPixel *0.345f,
            center = cellCenterOffset
        )
    }
    onLayer(2) {
        drawCircle(
            color = Color.DarkGray,
            radius = partsPixel * 0.33f,
            center = cellCenterOffset
        )
        glowPath[cell.position].toColor()?.let {
            drawCircle(
                color = it,
                radius = partsPixel * 0.1f,
                center = cellCenterOffset
            )
        }
    }
}

private fun CellDrawScope.drawSource(partsPixel: Float) {
    cell.source?.toColor()?.let {
        drawCircle(
            color = it,
            radius = partsPixel * 0.6f,
            center = cellCenterOffset
        )
    }
}

private fun CellDrawScope.drawEndpoint(
    partsPixel: Float,
    glowPath: GlowPath
) {
    cell.endPoint.toColor()?.let {
        val connectedColor = glowPath[cell.position]
        if (connectedColor.containsAll(cell.endPoint)) {
            onLayer(4){
                drawCircle(
                    color = Color.White,
                    radius = partsPixel *0.75f,
                    center = cellCenterOffset,
                    style = Stroke(partsPixel *0.2f)
                )
            }
        }

        drawCircle(
            color = it,
            radius = partsPixel / 3,
            center = cellCenterOffset,
            style = Stroke(partsPixel / 6)
        )
    }
}

private fun CellDrawScope.drawConnections(
    partsPixel: Float,
    glowPath: GlowPath
) {
    val connectedColor = glowPath[cell.position].toColor()
    cell.openCircleParts.forEach { circlePart ->
        val angleOffset = -90f - 360 / 12 / 2
        val startAngle = ((angleOffset + 360 * circlePart / 12) + cell.rotationWithParts * 360 / 6) % 360f
        val middleAngle = startAngle + 360 / 12 / 2
        onLayer(1) {
            drawLine(
                color = Color.White,
                start = cellCenterOffset,
                end = plusAngle(angle = middleAngle, length = partsPixel),
                strokeWidth = partsPixel * 0.36f
            )
        }
        onLayer(2){
            drawLine(
                color = Color.DarkGray,
                start = cellCenterOffset,
                end = plusAngle(angle = middleAngle, length = partsPixel),
                strokeWidth = partsPixel * 0.33f
            )
        }
        onLayer(3){
            connectedColor?.let {
                drawLine(
                    color = it,
                    start = cellCenterOffset,
                    end = plusAngle(angle = middleAngle, length = partsPixel),
                    strokeWidth = partsPixel * 0.1f
                )
            }
        }

    }
}

private fun CellDrawScope.plusAngle(
    length: Float,
    angle: Float // 0 => 3 oclock
) = cellCenterOffset + Offset(
    x = cos(angle.toRadians()) * length,
    y = sin(angle.toRadians()) * length
)

private fun Float.toRadians(): Float = this * 0.017453292519943295f

private fun CellDrawScope.drawCellBorder(partsPixel: Float) {
    drawCircle(
        color = Color.White,
        radius = partsPixel,
        center = cellCenterOffset,
        style = Stroke(partsPixel / 50)
    )
}

private fun CellDrawScope.drawCellLock(partsPixel: Float) {
    if (cell.locked) {
        drawCircle(
            color = Color.White,
            radius = partsPixel * 0.9f,
            center = cellCenterOffset,
            style = Stroke(partsPixel / 40)
        )
    }
}

private fun COLOR.toColor() =
    when (this) {
        COLOR.RED -> Color.Red
        COLOR.YELLOW -> Color.Yellow
        COLOR.BLUE -> Color.Blue
    }

private fun Set<COLOR>.toColor() =
    when (this) {
        setOf(COLOR.RED) -> Color.Red
        setOf(COLOR.RED, COLOR.BLUE) -> Color(0xffa818cc)
        setOf(COLOR.BLUE) -> Color.Blue
        setOf(COLOR.BLUE, COLOR.YELLOW) -> Color.Green
        setOf(COLOR.YELLOW) -> Color.Yellow
        setOf(COLOR.YELLOW, COLOR.RED) -> Color(0xffFF9900)
        setOf(COLOR.YELLOW, COLOR.RED, COLOR.BLUE) -> Color.White
        else -> null
    }

private fun DrawScope.onAllCells(grid: Grid, width: Float, function: CellDrawScope.() -> Unit) {
    val parts = grid.x * 2 + 3
    val partsPixel = width / parts
    val layerFutures: MutableList<Pair<Int, () -> Unit>> = mutableListOf()

    (0 until grid.x).forEach { x ->
        (0 until grid.y).forEach { y ->
            val cellDrawScope = CellDrawScope(
                drawScope = this,
                parts = parts,
                partsPixel = partsPixel,
                cell = grid[x, y],
                cellCenterOffset = Offset(
                    x = ((if (y.odd) 3 else 2) + x * 2) * partsPixel,
                    y = (1 + y) * sqrt((2 * partsPixel).pow(2) - partsPixel.pow(2))
                )
            )
            cellDrawScope.function()
            layerFutures += cellDrawScope.layerFutures
        }
    }
    layerFutures.sortedBy { (layer, _) -> layer }
        .forEach { (_, future) -> future() }
}

data class CellDrawScope(
    val drawScope: DrawScope,
    val parts: Int,
    val partsPixel: Float,
    val cell: Cell,
    val cellCenterOffset: Offset
) : DrawScope by drawScope {
    val layerFutures: MutableList<Pair<Int, () -> Unit>> = mutableListOf()
    fun onLayer(layer: Int, function: () -> Unit) {
        layerFutures += Pair(layer, function)
    }
}

private fun Cell.isOpen(circlePart: Int): Boolean =
    when (circlePart) {
        1 -> TOPRIGHT in connections
        3 -> RIGHT in connections
        5 -> BOTTOMRIGHT in connections
        7 -> BOTTOMLEFT in connections
        9 -> LEFT in connections
        11 -> TOPLEFT in connections
        else -> false
    }

private val Cell.openCircleParts: List<Int>
    get() {
        return (0 until 12).filter { isOpen(it) }
    }

private fun Size.maxSquare(): Size {
    val width = if (width > height) height else width
    return Size(width, width)
}