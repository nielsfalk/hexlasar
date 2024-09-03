package de.nielsfalk.laserhexagon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import de.nielsfalk.laserhexagon.*
import de.nielsfalk.laserhexagon.Direction.*
import de.nielsfalk.laserhexagon.ui.Color.Companion.Black
import de.nielsfalk.laserhexagon.ui.Color.Companion.White
import de.nielsfalk.laserhexagon.ui.Color.Companion.toColor
import de.nielsfalk.laserhexagon.ui.Color.Companion.winningColors
import de.nielsfalk.laserhexagon.ui.Grid.Companion.wrapBorderConnectionsAsCellsAgain
import de.nielsfalk.laserhexagon.ui.HexlaserEvent.RotateCell
import kotlin.math.*


@Composable
fun HexlaserCanvas(
    state: HexLaserState,
    onEvent: (HexlaserEvent) -> Unit,
) {
    var cellCenterPoints by remember { mutableStateOf(mapOf<Offset, Position>()) }
    Canvas(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = { offset ->
                    cellCenterPoints.cellCloseTo(offset)
                        ?.let { onEvent(RotateCell(it)) }
                },
                onLongPress = { offset ->
                    cellCenterPoints.cellCloseTo(offset)
                        ?.let { onEvent(HexlaserEvent.LockCell(it)) }
                }
            )
        }
            .fillMaxWidth()
            .fillMaxHeight()) {
        val grid = state.grid
        when {
            size.run { width > height } && grid.run { x < y && !started } -> {
                onEvent(HexlaserEvent.ToggleXYWithLevelGeneration(true))
            }

            size.run { width < height } && grid.run { x > y && !started } -> {
                onEvent(HexlaserEvent.ToggleXYWithLevelGeneration(false))
            }

            else -> {
                drawRect(color = Black, size = size)

                val cellDrawingData = CellDrawingData(
                    grid = grid.wrapBorderConnectionsAsCellsAgain(),
                    size = size
                )
                drawGame(state, cellDrawingData)
                cellCenterPoints = cellDrawingData.cellOffsets.associate { (offset, cell) ->
                    offset to cell.position
                }
            }
        }
    }
}

private fun DrawScope.drawGame(state: HexLaserState, cellDrawingData: CellDrawingData) {
    val grid = state.grid
    onAllCells(cellDrawingData) {
        drawCellBorder(partsPixel)
        drawCellLock(partsPixel)
        drawConnections(partsPixel, grid.glowPath)
        drawEndpoint(partsPixel, grid.glowPath)
        drawMiddlePoint(partsPixel, grid.glowPath)
        drawSource(partsPixel)
    }
    drawWinning(state.solvingAnimationSpendTime)
}

private fun DrawScope.drawWinning(solvingAnimationSpendTime: Int?) {
    solvingAnimationSpendTime?.let {
        val percentOfAnimation = it * 100000 / winningAnimationSpeed / max(size.width, size.height)
        (winningColors).forEachIndexed { idx, color ->
            val radius = (percentOfAnimation - idx) * 100f
            if (radius > 0)
                drawCircle(
                    color = color,
                    radius = radius,
                    style = Stroke(200f)
                )
        }
    }
}

private fun CellDrawScope.drawMiddlePoint(
    partsPixel: Float,
    glowPath: GlowPath
) {
    onLayer(1) {
        drawCircle(
            color = White,
            radius = partsPixel * 0.345f,
            center = cellCenterOffset
        )
    }
    onLayer(2) {
        drawCircle(
            color = Black,
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
            onLayer(1) {
                drawCircle(
                    color = White,
                    radius = partsPixel * 0.75f,
                    center = cellCenterOffset,
                    style = Stroke(partsPixel * 0.2f)
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
    val connectedColor = glowPath[cell.position]?.toColor()
    cell.openCircleParts.forEach { circlePart ->
        val angleOffset = -90f - 360 / 12 / 2
        val startAngle = ((angleOffset + 360 * circlePart / 12) + cell.rotationWithParts * 360 / 6) % 360f
        val middleAngle = startAngle + 360 / 12 / 2
        onLayer(1) {
            drawLine(
                color = White,
                start = cellCenterOffset,
                end = plusAngle(angle = middleAngle, length = partsPixel),
                strokeWidth = partsPixel * 0.36f
            )
        }
        onLayer(2) {
            drawLine(
                color = Black,
                start = cellCenterOffset,
                end = plusAngle(angle = middleAngle, length = partsPixel),
                strokeWidth = partsPixel * 0.33f
            )
        }
        onLayer(3) {
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
        color = White,
        radius = partsPixel,
        center = cellCenterOffset,
        style = Stroke(partsPixel / 50)
    )
}

private fun CellDrawScope.drawCellLock(partsPixel: Float) {
    if (cell.locked) {
        drawCircle(
            color = White,
            radius = partsPixel * 0.9f,
            center = cellCenterOffset,
            style = Stroke(partsPixel / 40)
        )
    }
}

private data class CellDrawingData(
    val grid: Grid,
    val size: Size,
    val horizontalParts: Int = grid.x * 2 + 3,
    val verticalParts: Float = grid.y * 2 + 0.7f,
    val radius: Float = min(size.width / horizontalParts, size.height / verticalParts),
    val cellOffsets: List<Pair<Offset, Cell>> =
        (0 until grid.x).flatMap { x ->
            (0 until grid.y).map { y ->
                Offset(
                    x = ((if (y.odd) 3 else 2) + x * 2) * radius,
                    y = (1 + y) * sqrt((2 * radius).pow(2) - radius.pow(2))
                ) to grid[x, y]
            }
        }
)

private fun DrawScope.onAllCells(
    cellDrawingData: CellDrawingData,
    function: CellDrawScope.() -> Unit
) {
    val layerFutures: MutableList<Pair<Int, () -> Unit>> = mutableListOf()
    cellDrawingData.cellOffsets.forEach { (offset, cell) ->
        val cellDrawScope = CellDrawScope(
            drawScope = this,
            partsPixel = cellDrawingData.radius,
            cell = cell,
            cellCenterOffset = offset
        )
            .apply(function)
        layerFutures += cellDrawScope.layerFutures
    }
    layerFutures.sortedBy { (layer, _) -> layer }
        .forEach { (_, future) -> future() }
}

data class CellDrawScope(
    val drawScope: DrawScope,
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

internal fun Map<Offset, Position>.cellCloseTo(tapOffset: Offset): Position? =
    keys.minByOrNull { (it.x - tapOffset.x).absoluteValue + (it.y - tapOffset.y).absoluteValue }
        ?.let { this[it] }

private class Grid private constructor(
    private val wrapped: de.nielsfalk.laserhexagon.Grid
) {
    operator fun get(x: Int, y: Int): Cell =
        if (x < wrapped.x && y < wrapped.y) {
            wrapped[x, y]
        } else {
            wrapped[x % wrapped.x, y % wrapped.y]
        }

    val x: Int = if (wrapped.connectBorders) wrapped.x + 1 else wrapped.x
    val y: Int = if (wrapped.connectBorders) wrapped.y + 1 else wrapped.y

    companion object {
        fun de.nielsfalk.laserhexagon.Grid.wrapBorderConnectionsAsCellsAgain() = Grid(this)
    }
}
