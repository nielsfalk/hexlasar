package de.nielsfalk.laserhexagon.ui

import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import de.nielsfalk.laserhexagon.LevelType

private val HexLaserState.showLevelUpDialog: Boolean
    get() = solvingCount[levelType]!! == 3 &&
            grid.cells.all { it.locked } &&
            grid.solved &&
            levelType != LevelType.entries.last()

@Composable
fun LevelUpDialog(
    state: HexLaserState,
    onEvent: (HexlaserEvent) -> Unit
) {
    if (state.showLevelUpDialog) {
        AlertDialog(
            title = {
                Text(text = "You solved it ${state.solvingCount[state.levelType]} times on level ${state.levelType.ordinal + 1} ")
            },
            text = {
                Text(text = "Do you want to level up?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(HexlaserEvent.LevelUp)
                    }
                ) {
                    Text("Level up")
                    Icon(imageVector = Icons.up, contentDescription = null)
                }
            },
            onDismissRequest = {
                onEvent(HexlaserEvent.NextGrid)
            },
            dismissButton = {
                TextButton(
                    onClick = { onEvent(HexlaserEvent.NextGrid) }
                ) {
                    Text("Continue on Level ${state.levelType.ordinal + 1}")
                    Icon(imageVector = Icons.right, contentDescription = null)
                }
            }
        )
    }
}