package de.nielsfalk.laserhexagon

import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        var text by remember { mutableStateOf("event") }
        var cellCenterPoints = mapOf<Offset, Cell>()
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { text = "!showContent" }) {
                Text("Click me! $text")
            }
            val grid = testGrid
            GameCanvas(
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { tapOffset ->
                            cellCenterPoints.cellCloseTo(tapOffset)
                                ?.let { text = "${it.position.x} ${it.position.y}" }
                        }
                    )
                }
                    .weight(1f)
                    .aspectRatio(1f),
                grid = grid,
                leakCellCenterPoints = { cellCenterPoints = it })
        }
    }
}
