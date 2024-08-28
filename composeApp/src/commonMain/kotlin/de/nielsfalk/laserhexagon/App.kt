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
                val maxHexGridX = 5
                val maxHexGridY = (maxHexGridX * 1.3).toInt() - 1
                val parts = maxHexGridX * 2 + 3
                val partsPixel = size.width / parts
                (0 until maxHexGridX).forEach { x ->
                    (0 until maxHexGridY).forEach { y ->
                        val cellCenterOffset = Offset(
                            x = ((if (y.odd) 3 else 2) + x * 2) * partsPixel,
                            y = (1 + y) * sqrt((2 * partsPixel).pow(2) - partsPixel.pow(2))
                        )
                        (0..12).forEach {
                            val angleOffset = -90f - 360 / 12 / 2
                            val startAngle = angleOffset + 360 * it / 12
                            drawArc(
                                color =
                                when (it % 3) {
                                    0 -> Color.White
                                    1 -> Color.Blue
                                    else -> Color.Green
                                },
                                topLeft = cellCenterOffset - Offset(partsPixel, partsPixel),
                                size = Size(partsPixel * 2, partsPixel * 2),
                                startAngle = startAngle,
                                sweepAngle = 360f / 12,
                                useCenter = false,
                                style = Stroke(partsPixel / 50)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun Size.maxSquare(): Size {
    val width = if (width > height) height else width
    return Size(width, width)
}