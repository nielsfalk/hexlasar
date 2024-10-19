package de.nielsfalk.laserhexagon.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import de.nielsfalk.laserhexagon.LevelType
import de.nielsfalk.laserhexagon.ui.Color.Companion.Black

@Composable
fun HexlaserScreen(
    state: HexLaserState,
    onEvent: (HexlaserEvent) -> Unit
) {

    Column(
        Modifier.fillMaxWidth()
            .background(Black)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        horizontalAlignment = CenterHorizontally
    ) {
        Buttons(onEvent, state)
        LevelUpDialog(state, onEvent)
        HexlaserCanvas(
            state = state,
            onEvent = onEvent,
        )
    }
}

