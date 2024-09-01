package de.nielsfalk.laserhexagon.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.unit.dp
import de.nielsfalk.laserhexagon.ui.GameEvent.*

@Composable
fun GameScreen(
    state: GameState,
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
                Text(if (state.grid.solved) "You solved it" else "retry")
            }
            Button(
                onClick = { onEvent(Next) },
                modifier = Modifier.padding(horizontal = 5.dp)
            ) {
                Text("next")
            }
        }
        GameCanvas(
            state = state,
            onEvent = onEvent,
        )
    }
}
