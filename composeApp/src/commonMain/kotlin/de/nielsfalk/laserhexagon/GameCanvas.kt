package de.nielsfalk.laserhexagon

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import de.nielsfalk.laserhexagon.Direction.BOTTOMLEFT
import de.nielsfalk.laserhexagon.Direction.BOTTOMRIGHT
import de.nielsfalk.laserhexagon.Direction.LEFT
import de.nielsfalk.laserhexagon.Direction.RIGHT
import de.nielsfalk.laserhexagon.Direction.TOPLEFT
import de.nielsfalk.laserhexagon.Direction.TOPRIGHT
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun GameCanvas(modifier: Modifier, grid: Grid, leakCellCenterPoints: (Map<Offset, Position>) -> Unit) {
    Canvas(
        modifier = modifier,
    ) {
        drawRect(color = Color.Black, size = size.maxSquare())

        val parts = grid.x * 2 + 3
        val partsPixel = size.width / parts
        grid.onAllCells(size.width) {
            drawCircle(
                color = Color.White,
                radius = partsPixel,
                center = cellCenterOffset,
                style = Stroke(partsPixel / 50)
            )
        }
        grid.onAllCells(size.width) {
            val connectedColor = cell.connected.toColor()

            cell.openCircleParts.forEach { circlePart ->
                val angleOffset = -90f - 360 / 12 / 2
                val startAngle = ((angleOffset + 360 * circlePart / 12) + cell.rotationWithParts * 360 / 6) % 360f
                drawArc(
                    color = Color.White,
                    topLeft = cellCenterOffset - Offset(partsPixel * 0.50f, partsPixel * 0.50f),
                    size = Size(partsPixel, partsPixel),
                    startAngle = startAngle - 1,
                    sweepAngle = 360f / 12 + 2,
                    useCenter = false,
                    style = Stroke(partsPixel)
                )
                connectedColor?.let {
                    drawArc(
                        color = it,
                        topLeft = cellCenterOffset - Offset(partsPixel * 0.55f, partsPixel * 0.55f),
                        size = Size(partsPixel * 1.1f, partsPixel * 1.1f),
                        startAngle = startAngle,
                        sweepAngle = 360f / 12,
                        useCenter = false,
                        style = Stroke(partsPixel)
                    )
                }
                    ?: drawArc(
                        color = Color.DarkGray,
                        topLeft = cellCenterOffset - Offset(partsPixel * 0.52f, partsPixel * 0.52f),
                        size = Size(partsPixel * 1.04f, partsPixel * 1.04f),
                        startAngle = startAngle,
                        sweepAngle = 360f / 12,
                        useCenter = false,
                        style = Stroke(partsPixel)
                    )

            }
            cell.endPoint.toColor()?.let {
                drawCircle(
                    color = it,
                    radius = partsPixel / 3,
                    center = cellCenterOffset,
                    style = Stroke(partsPixel / 6)
                )
            }
            drawCircle(
                color = connectedColor ?: Color.DarkGray,
                radius = partsPixel / 3,
                center = cellCenterOffset
            )
            cell.source?.toColor()?.let {
                drawCircle(
                    color = it,
                    radius = partsPixel * 0.6f,
                    center = cellCenterOffset
                )
            }
        }
        leakCellCenterPoints(mutableMapOf<Offset, Position>().apply {
            grid.onAllCells(this@Canvas.size.width) {
                put(cellCenterOffset, cell.position)
            }
        }
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

private fun Grid.onAllCells(width: Float, function: CellDrawContext.() -> Unit) {
    val parts = x * 2 + 3
    val partsPixel = width / parts

    (0 until x).forEach { x ->
        (0 until y).forEach { y ->
            CellDrawContext(
                parts = parts,
                partsPixel = partsPixel,
                cell = this[x, y],
                cellCenterOffset = Offset(
                    x = ((if (y.odd) 3 else 2) + x * 2) * partsPixel,
                    y = (1 + y) * sqrt((2 * partsPixel).pow(2) - partsPixel.pow(2))
                )
            ).function()
        }
    }
}

data class CellDrawContext(
    val parts: Int,
    val partsPixel: Float,
    val cell: Cell,
    val cellCenterOffset: Offset
)

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