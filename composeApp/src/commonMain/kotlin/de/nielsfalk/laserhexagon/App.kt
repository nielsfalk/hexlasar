package de.nielsfalk.laserhexagon

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                drawRect(color = Color.Black, size = size.maxSquare())
                val grid = testGrid
                val parts = grid.x * 2 + 3
                val partsPixel = size.width / parts
                grid.onAllCells(size.width) {
                    drawCircle(
                        color = Color.White,
                        radius = partsPixel,
                        center = cellCenterOffset,
                        style = Stroke(partsPixel/50 )
                    )
                }
                grid.onAllCells(size.width) {
                    cell.openCircleParts.forEach { circlePart ->
                        val angleOffset = -90f - 360 / 12 / 2
                        val startAngle = angleOffset + 360 * circlePart / 12
                        drawArc(
                            color = Color.DarkGray,
                            topLeft = cellCenterOffset - Offset(partsPixel * 0.55f, partsPixel * 0.55f),
                            size = Size(partsPixel * 1.1f, partsPixel * 1.1f),
                            startAngle = startAngle,
                            sweepAngle = 360f / 12,
                            useCenter = false,
                            style = Stroke(partsPixel)
                        )
                    }
                    drawCircle(
                        color = Color.DarkGray,
                        radius = partsPixel/3,
                        center = cellCenterOffset
                    )
                }
            }
        }
    }
}

private fun Grid.onAllCells(width: Float, function: CellDrawContext.() -> Unit) {
    val parts = x * 2 + 3
    val partsPixel = width / parts

    (0 until x).forEach { x ->
        (0 until y).forEach { y ->
            CellDrawContext(
                parts = parts,
                partsPixel = partsPixel,
                cell = cells[x][y],
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