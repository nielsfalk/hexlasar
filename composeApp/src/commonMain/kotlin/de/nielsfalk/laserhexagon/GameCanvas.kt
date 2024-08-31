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
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun GameCanvas(
    modifier: Modifier,
    grid: Grid,
    leakCellCenterPoints: (Map<Offset, Position>) -> Unit,
    toggleXYWithLevelGeneration: (Boolean) -> Unit,
) {
    Canvas(
        modifier = modifier
    ) {
        when {
            size.run { width > height } && grid.run { x < y } -> {
                toggleXYWithLevelGeneration(true)
            }

            size.run { width < height } && grid.run { x > y } -> {
                toggleXYWithLevelGeneration(false)
            }

            else -> {
                drawRect(color = Color.Black, size = size)

                val parts = grid.x * 2 + 3
                val partsPixel = size.width / parts
                drawWhiteCellBorders(grid, partsPixel)
                drawCellLock(grid, partsPixel)
                drawConnections(grid, partsPixel)
                drawEndpoints(grid, partsPixel)
                drawMiddlePoint(grid, partsPixel)
                drawSource(grid, partsPixel)

                leakCellCenterPoints(
                    mutableMapOf<Offset, Position>().apply {
                        this@Canvas.onAllCells(grid, this@Canvas.size.width) {
                            put(cellCenterOffset, cell.position)
                        }
                    }
                )
            }
        }
    }
}

private fun DrawScope.drawMiddlePoint(grid: Grid, partsPixel: Float) {
    this.onAllCells(grid, size.width) {
        val connectedColor = grid.glowPath[cell.position].toColor()
        drawCircle(
            color = connectedColor ?: Color.DarkGray,
            radius = partsPixel / 3,
            center = cellCenterOffset
        )
    }
}

private fun DrawScope.drawSource(grid: Grid, partsPixel: Float) {
    this.onAllCells(grid, size.width) {
        cell.source?.toColor()?.let {
            drawCircle(
                color = it,
                radius = partsPixel * 0.6f,
                center = cellCenterOffset
            )
        }
    }
}

private fun DrawScope.drawEndpoints(grid: Grid, partsPixel: Float) {
    this.onAllCells(grid, size.width) {

        cell.endPoint.toColor()?.let {
            val connectedColor = grid.glowPath[cell.position]
            if (connectedColor.containsAll(cell.endPoint)) {
                drawCircle(
                    color = Color.White,
                    radius = partsPixel / 3,
                    center = cellCenterOffset,
                    style = Stroke(partsPixel / 2)
                )
            }

            drawCircle(
                color = it,
                radius = partsPixel / 3,
                center = cellCenterOffset,
                style = Stroke(partsPixel / 6)
            )
        }
    }
}

private fun DrawScope.drawConnections(grid: Grid, partsPixel: Float) {
    this.onAllCells(grid, size.width) {
        cell.openCircleParts.forEach { circlePart ->
            val angleOffset = -90f - 360 / 12 / 2
            val startAngle = ((angleOffset + 360 * circlePart / 12) + cell.rotationWithParts * 360 / 6) % 360f
            val middleAngle = startAngle + 360 / 12 / 2
            drawLine(
                color = Color.White,
                start = cellCenterOffset,
                end = plusAngle(angle = middleAngle, length = partsPixel),
                strokeWidth = partsPixel * 0.36f
            )
        }
    }
    this.onAllCells(grid, size.width) {
        val connectedColor = grid.glowPath[cell.position].toColor()
        cell.openCircleParts.forEach { circlePart ->
            val angleOffset = -90f - 360 / 12 / 2
            val startAngle = ((angleOffset + 360 * circlePart / 12) + cell.rotationWithParts * 360 / 6) % 360f
            val middleAngle = startAngle + 360 / 12 / 2
            drawLine(
                color = Color.DarkGray,
                start = cellCenterOffset,
                end = plusAngle(angle = middleAngle, length = partsPixel),
                strokeWidth = partsPixel * 0.33f
            )
            connectedColor?.let {
                drawLine(
                    color = it,
                    start = cellCenterOffset,
                    end = plusAngle(angle = middleAngle, length = partsPixel),
                    strokeWidth = partsPixel * 0.1f
                )
            }

                ?: drawArc(
                    color = Color.DarkGray,
                    topLeft = cellCenterOffset - Offset(partsPixel * 0.52f, partsPixel * 0.52f),
                    size = Size(partsPixel * 1.04f, partsPixel * 1.04f),
                    startAngle = middleAngle - 1,
                    sweepAngle = 2f,
                    useCenter = false,
                    style = Stroke(partsPixel, miter = 20f)
                )

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

private fun DrawScope.drawWhiteCellBorders(grid: Grid, partsPixel: Float) {
    this.onAllCells(grid, size.width) {
        drawCircle(
            color = Color.White,
            radius = partsPixel,
            center = cellCenterOffset,
            style = Stroke(partsPixel / 50)
        )
    }
}

private fun DrawScope.drawCellLock(grid: Grid, partsPixel: Float) {
    this.onAllCells(grid, size.width) {
        if (cell.locked) {
            drawCircle(
                color = Color.White,
                radius = partsPixel * 0.9f,
                center = cellCenterOffset,
                style = Stroke(partsPixel / 40)
            )
        }
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

    (0 until grid.x).forEach { x ->
        (0 until grid.y).forEach { y ->
            CellDrawScope(
                drawScope = this,
                parts = parts,
                partsPixel = partsPixel,
                cell = grid[x, y],
                cellCenterOffset = Offset(
                    x = ((if (y.odd) 3 else 2) + x * 2) * partsPixel,
                    y = (1 + y) * sqrt((2 * partsPixel).pow(2) - partsPixel.pow(2))
                )
            ).function()
        }
    }
}

data class CellDrawScope(
    val drawScope: DrawScope,
    val parts: Int,
    val partsPixel: Float,
    val cell: Cell,
    val cellCenterOffset: Offset
) : DrawScope by drawScope

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