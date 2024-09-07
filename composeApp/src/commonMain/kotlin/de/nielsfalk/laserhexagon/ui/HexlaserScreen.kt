package de.nielsfalk.laserhexagon.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.nielsfalk.laserhexagon.LevelType
import de.nielsfalk.laserhexagon.ui.Color.Companion.Black
import de.nielsfalk.laserhexagon.ui.HexlaserEvent.LevelUp
import de.nielsfalk.laserhexagon.ui.HexlaserEvent.NextGrid
import de.nielsfalk.laserhexagon.ui.Icons.Companion.right
import de.nielsfalk.laserhexagon.ui.Icons.Companion.up

private val HexLaserState.showLevelUpDialog: Boolean
    get() = solvingCount[levelType]!! == 3 &&
            grid.cells.all { it.locked } &&
            grid.solved &&
            levelType != LevelType.entries.last()

@Composable
fun HexlaserScreen(
    state: HexLaserState,
    onEvent: (HexlaserEvent) -> Unit
) {

    Column(Modifier.fillMaxWidth().background(Black), horizontalAlignment = Alignment.CenterHorizontally) {
        Buttons(onEvent, state)
        if (state.showLevelUpDialog) {
            AlertDialog(
                title = {
                    Text(text = "You solved it ${state.solvingCount[state.levelType]} times on level ${state.levelType.ordinal+1} ")
                },
                text = {
                    Text(text = "Do you want to level up?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onEvent(LevelUp)
                        }
                    ) {
                        Text("Level up")
                        Icon(imageVector = up, contentDescription = null)
                    }
                },
                onDismissRequest = {
                    onEvent(NextGrid)
                },
                dismissButton = {
                    TextButton(
                        onClick = { onEvent(NextGrid) }
                    ) {
                        Text("Continue on Level ${state.solvingCount[state.levelType]}")
                        Icon(imageVector = right, contentDescription = null)
                    }
                }
            )
        }
        HexlaserCanvas(
            state = state,
            onEvent = onEvent,
        )
    }
}
