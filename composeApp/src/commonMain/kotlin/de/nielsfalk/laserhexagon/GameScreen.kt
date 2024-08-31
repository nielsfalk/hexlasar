package de.nielsfalk.laserhexagon

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import de.nielsfalk.laserhexagon.GameEvent.*
import kotlin.math.absoluteValue

@Composable
fun GameScreen(
    state: Grid,
    onEvent: (GameEvent) -> Unit
) {

    Column(Modifier.fillMaxWidth().background(Black), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            Modifier
                .padding(horizontal = 5.dp)
                .fillMaxWidth()
        ) {

            Button(
                onClick = { onEvent(LevelUp) },
                modifier = Modifier.padding(horizontal = 5.dp)
            ) {
                Text(state.levelType.lable)
            }
            Button(
                onClick = { onEvent(Retry) },
                modifier = Modifier.padding(horizontal = 5.dp)
            ) {
                Text(if (state.solved) "You solved it" else "retry")
            }
            Button(
                onClick = { onEvent(Next) },
                modifier = Modifier.padding(horizontal = 5.dp)
            ) {
                Text("next")
            }
        }
        GameCanvas(
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onEvent(RotateCell(it))},
                    onLongPress = { onEvent(LockCell(it)) }
                )
            }
                .fillMaxWidth()
                .fillMaxHeight(),
            grid = state,
            onEvent=onEvent,
        )
    }
}

internal fun Map<Offset, Position>.cellCloseTo(tapOffset: Offset): Position? =
    keys.minByOrNull { (it.x - tapOffset.x).absoluteValue + (it.y - tapOffset.y).absoluteValue }
        ?.let { this[it] }
