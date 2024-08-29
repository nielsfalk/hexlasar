package de.nielsfalk.laserhexagon

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.absoluteValue

@Composable
fun GameScreen(onRetry: () -> Unit, onTabCell: (Position) -> Unit, state: Grid) {
    var cellCenterPoints = mapOf<Offset, Position>()

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { onRetry() }) {
            Text(if (state.solved) "You solved it" else "retry")
        }
        GameCanvas(
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = { tapOffset ->
                        cellCenterPoints.cellCloseTo(tapOffset)
                            ?.let {
                                onTabCell(it)
                            }
                    }
                )
            }
                .weight(1f)
                .aspectRatio(1f),
            grid = state,
            leakCellCenterPoints = {
                cellCenterPoints = it
            })
    }
}

internal fun Map<Offset, Position>.cellCloseTo(tapOffset: Offset): Position? =
    keys.minByOrNull { (it.x - tapOffset.x).absoluteValue + (it.y - tapOffset.y).absoluteValue }
        ?.let { this[it] }
