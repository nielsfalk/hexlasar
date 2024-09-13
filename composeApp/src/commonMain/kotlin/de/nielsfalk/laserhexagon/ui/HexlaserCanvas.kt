package de.nielsfalk.laserhexagon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import de.nielsfalk.laserhexagon.ui.HexlaserEvent.DragCell
import de.nielsfalk.laserhexagon.ui.HexlaserEvent.TabCell
import de.nielsfalk.util.LayerDrawScope
import de.nielsfalk.util.layers
import kotlin.math.*


@Composable
fun HexlaserCanvas(
    state: HexLaserState,
    onEvent: (HexlaserEvent) -> Unit,
) {
    var cellCenterPoints by remember { mutableStateOf(listOf<Pair<Offset, Position>>()) }
    var ongoingDrag by remember { mutableStateOf<Pair<Position, Float>?>(null) }
    Box(modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(
            onTap = { offset ->
                cellCenterPoints.cellCloseTo(offset)
                    ?.let { onEvent(TabCell(it)) }
            },
            onLongPress = { offset ->
                cellCenterPoints.cellCloseTo(offset)
                    ?.let { onEvent(HexlaserEvent.LockCell(it)) }
            }
        )
    }) {
        Canvas(
            modifier = Modifier.pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        cellCenterPoints.cellCloseTo(offset)?.let {
                            ongoingDrag = it to 0f
                        }
                    },
                    onVerticalDrag = { change, dragAmount ->
                        ongoingDrag?.let { (position, value) ->
                            ongoingDrag = position to value + dragAmount * 360 / 1000
                        }
                    },
                    onDragCancel = {
                        ongoingDrag = null
                    },
                    onDragEnd = {
                        ongoingDrag?.let { (position, degrees) ->
                            var positiveDegrees = degrees
                            while (positiveDegrees < 0) positiveDegrees += 360
                            onEvent(
                                DragCell(
                                    rotations = (positiveDegrees.toInt() + 30) / 60,
                                    position = position
                                )
                            )
                        }
                        ongoingDrag = null
                    }
                )

            }
                .fillMaxSize()) {
            val grid = state.grid
            when {
                size.run { width > height } && grid.run { x < y && !started } -> {
                    onEvent(HexlaserEvent.ToggleXYWithLevelGeneration(true))
                }

                size.run { width < height } && grid.run { x > y && !started } -> {
                    onEvent(HexlaserEvent.ToggleXYWithLevelGeneration(false))
                }

                else -> {
                    val cellDrawingData = CellDrawingData(
                        grid = grid.wrapBorderConnectionsAsCellsAgain(),
                        size = size
                    )
                    drawGame(state, cellDrawingData, ongoingDrag)
                    cellCenterPoints = cellDrawingData.cellOffsets.map { (offset, cell) ->
                        offset to cell.position
                    }
                }
            }
        }

    }
}

private fun DrawScope.drawGame(
    state: HexLaserState,
    cellDrawingData: CellDrawingData,
    ongoingDrag: Pair<Position, Float>?
) {
    layers {
        onAllCells(cellDrawingData) {
            drawCellBorder(partsPixel)
            drawCellLock(partsPixel)
            drawConnections(partsPixel, state.grid.glowPath, ongoingDrag)
            drawEndpoint(partsPixel, state.grid.glowPath)
            drawMiddlePoint(partsPixel, state.grid.glowPath)
            drawPrisma(partsPixel)
            drawSource(partsPixel)
        }
        drawInfiniteBorders(cellDrawingData, state.grid.infiniteX, state.grid.infiniteY)
    }
    drawWinning(state.animationSpendTime)
}

private fun LayerDrawScope.drawInfiniteBorders(
    cellDrawingData: CellDrawingData,
    infiniteX: Boolean,
    infiniteY: Boolean
) {
    if (infiniteX || infiniteY) {
        onLayer(5) {
            cellDrawingData.run {
                val (topLeftCellCenter, _) = cellOffsets.first { (_, cell) -> cell.position == Position(0, 0) }
                if (infiniteX) {
                    val leftLineStart = topLeftCellCenter.copy(y = topLeftCellCenter.y - radius)
                    val leftLineEnd =
                        topLeftCellCenter.copy(y = topLeftCellCenter.y + radius + cellHeight * (grid.y - 1))
                    drawRect(
                        color = Black,
                        topLeft = Offset(0f, 0f),
                        size = Size(width = leftLineStart.x, height = size.height)
                    )
                    drawLine(
                        color = White,
                        strokeWidth = radius / 42,
                        start = leftLineStart,
                        end = leftLineEnd
                    )
                    val rightLineStart = topLeftCellCenter.copy(
                        x = grid.x * 2 * radius + topLeftCellCenter.x - radius,
                        y = topLeftCellCenter.y - radius
                    )
                    val rightLineEnd = topLeftCellCenter.copy(
                        x = grid.x * 2 * radius + topLeftCellCenter.x - radius,
                        y = topLeftCellCenter.y + radius + cellHeight * (grid.y - 1)
                    )
                    drawRect(
                        color = Black,
                        topLeft = rightLineStart,
                        size = size
                    )
                    drawLine(
                        color = White,
                        strokeWidth = radius / 42,
                        start = rightLineStart,
                        end = rightLineEnd
                    )
                }
                if (infiniteY) {
                    val leftLineStart = topLeftCellCenter.copy(y = topLeftCellCenter.y - radius / 2)
                    val leftLineEnd = leftLineStart.copy(x = (grid.x - 0.4f) * radius * 2)
                    drawRect(
                        color = Black,
                        topLeft = Offset(0f, 0f),
                        size = Size(width = size.width, height = leftLineStart.y)
                    )
                    drawLine(
                        color = White,
                        strokeWidth = radius / 42,
                        start = leftLineStart,
                        end = leftLineEnd
                    )
                    val bottomLineStart =
                        topLeftCellCenter.copy(y = topLeftCellCenter.y + radius + cellHeight * (grid.y - 1) - radius / 2)
                    val bottomLineEnd = bottomLineStart.copy(x = (grid.x - 0.4f) * radius * 2)
                    drawRect(
                        color = Black,
                        topLeft = Offset(0f, bottomLineStart.y),
                        size = size
                    )
                    drawLine(
                        color = White,
                        strokeWidth = radius / 42,
                        start = bottomLineStart,
                        end = bottomLineEnd
                    )

                }
            }
        }
    }

}

private fun DrawScope.drawWinning(animationSpendTime: Int?) {
    animationSpendTime?.let {
        val percentOfAnimation = it * 100000 / animationSpeed / max(size.width, size.height)
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
    }
    onLayer(4) {
        glowPath.colors(cell.position).toColor()?.let {
            drawCircle(
                color = if (cell.prisma) White else it,
                radius = partsPixel * 0.1f,
                center = cellCenterOffset
            )
        }
    }
}

private fun CellDrawScope.drawPrisma(partsPixel: Float) {
    if (cell.prisma) {
        onLayer(4) {
            drawCircle(
                color = White,
                radius = partsPixel * 0.345f,
                center = cellCenterOffset,
                style = Stroke(partsPixel / 50)
            )
            drawPath(
                Path().apply {
                    val x = cellCenterOffset.x
                    val y = cellCenterOffset.y
                    val top = y - partsPixel * 0.345f
                    moveTo(x = x, y = top)
                    val h = partsPixel * 0.345f * sqrt(3f) * sqrt(3f) / 2

                    lineTo(x = x + h / 2, y = y + h - partsPixel * 0.345f)
                    lineTo(x = x - h / 2, y = y + h - partsPixel * 0.345f)
                    lineTo(x = x, y = top)
                },
                color = White,
                style = Stroke(partsPixel / 50)
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
        val connectedColor = glowPath.colors(cell.position)
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
    glowPath: GlowPath,
    ongoingDrag: Pair<Position, Float>?
) {
    cell.openCircleParts.forEach { (circlePart, direction) ->
        val angleOffset = -90f - 360 / 12 / 2
        val ongoingCellDrag = ongoingDrag?.let { (position, degrees) ->
            if (!cell.locked && cell.position == position) degrees else 0f
        } ?: 0f
        val startAngle =
            ((angleOffset + 360 * circlePart / 12) + cell.rotationWithParts * 360 / 6) % 360f + ongoingCellDrag
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
            val connectedColor: Color? =
                if (cell.prisma) {
                    glowPath[cell.position].flatMap {
                        it.prismaFrom?.let { prismaFrom ->
                            if (direction.rotate(cell.rotations) == prismaFrom) {
                                val sourceNe = cell.neighborsPositions[prismaFrom]
                                sourceNe?.let { glowPath.colors(it) }
                            } else setOf(it.color)
                        }
                            ?: setOf(it.color)
                    }.toSet().toColor()
                } else {
                    glowPath.colors(cell.position).toColor()
                }
            connectedColor?.let {
                drawLine(
                    color = connectedColor,
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

private fun Float.toRadians(): Float = this * 0.017453292f

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
            radius = partsPixel * 0.97f,
            center = cellCenterOffset,
            style = Stroke(partsPixel / 25)
        )
    }
}

private data class CellDrawingData(
    val grid: Grid,
    val size: Size,
    val horizontalParts: Float = grid.x * 2 + if (grid.infiniteX) -0.6f else 1f,
    val verticalParts: Float = (if (grid.infiniteY) 2f else 2.5f) +
            sqrt((2 * (grid.y - 1f)).pow(2) - (grid.y - 1f).pow(2)),
    val radius: Float = min(size.width / horizontalParts, size.height / verticalParts),
    val cellHeight: Float = sqrt((2 * radius).pow(2) - radius.pow(2)),
    val cellOffsets: List<Pair<Offset, Cell>> =
        (0 until grid.x).flatMap { x ->
            (0 until grid.y).map { y ->
                Offset(
                    x = ((when {
                        grid.infiniteX && y.odd -> 1.2f
                        grid.infiniteX -> 0.2f
                        y.odd -> 2f
                        else -> 1f
                    }) + x * 2) * radius,
                    y = (0.7f + y) * cellHeight
                ) to grid[x, y]
            }
        }
)

private fun LayerDrawScope.onAllCells(
    cellDrawingData: CellDrawingData,
    function: CellDrawScope.() -> Unit
) {
    cellDrawingData.cellOffsets.forEach { (offset, cell) ->
        CellDrawScope(
            drawScope = this,
            partsPixel = cellDrawingData.radius,
            cell = cell,
            cellCenterOffset = offset
        )
            .apply(function)
    }
}

data class CellDrawScope(
    val drawScope: LayerDrawScope,
    val partsPixel: Float,
    val cell: Cell,
    val cellCenterOffset: Offset
) : LayerDrawScope(drawScope)

private val Cell.openCircleParts: List<Pair<Int, Direction>>
    get() {
        return listOfNotNull(
            if (TOPRIGHT in connections) 1 to TOPRIGHT else null,
            if (RIGHT in connections) 3 to RIGHT else null,
            if (BOTTOMRIGHT in connections) 5 to BOTTOMRIGHT else null,
            if (BOTTOMLEFT in connections) 7 to BOTTOMLEFT else null,
            if (LEFT in connections) 9 to LEFT else null,
            if (TOPLEFT in connections) 11 to TOPLEFT else null
        )
    }

internal fun List<Pair<Offset, Position>>.cellCloseTo(tapOffset: Offset): Position? =
    minByOrNull { (offset, _) -> (offset.x - tapOffset.x).absoluteValue + (offset.y - tapOffset.y).absoluteValue }
        ?.let { (_, position) -> position }

private class Grid private constructor(
    private val wrapped: de.nielsfalk.laserhexagon.Grid
) {
    val infiniteY = wrapped.infiniteY
    val infiniteX = wrapped.infiniteX

    operator fun get(x: Int, y: Int): Cell =
        if (x < wrapped.x && y < wrapped.y) {
            wrapped[x, y]
        } else {
            wrapped[x % wrapped.x, y % wrapped.y]
        }

    val x: Int = if (infiniteX) wrapped.x + 1 else wrapped.x

    val y: Int = if (infiniteY) wrapped.y + 1 else wrapped.y

    companion object {
        fun de.nielsfalk.laserhexagon.Grid.wrapBorderConnectionsAsCellsAgain() = Grid(this)
    }
}