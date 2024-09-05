package de.nielsfalk.laserhexagon.ui

import androidx.compose.ui.graphics.Color as ComposeColor
import de.nielsfalk.laserhexagon.Color as CellColor

class Color {
    companion object {
        val White = ComposeColor.White
        val Yellow = ComposeColor.Yellow
        val Orange = ComposeColor(0xffFF9900)
        val Red = ComposeColor.Red
        val Purple = ComposeColor(0xffa818cc)
        val Blue = ComposeColor.Blue
        val Green = ComposeColor.Green
        val Black = ComposeColor.Black

        private val usedColorsToCellColors: List<Pair<ComposeColor, Set<CellColor>>> = listOf(
            White to setOf(CellColor.Red, CellColor.Yellow, CellColor.Blue),
            Yellow to setOf(CellColor.Yellow),
            Orange to setOf(CellColor.Yellow, CellColor.Red),
            Red to setOf(CellColor.Red),
            Purple to setOf(CellColor.Red, CellColor.Blue),
            Blue to setOf(CellColor.Blue),
            Green to setOf(CellColor.Blue, CellColor.Yellow)
        )

        fun Set<CellColor>.toColor(): ComposeColor? =
            usedColorsToCellColors.firstOrNull() { (_, cellColor) -> cellColor == this }?.first

        fun CellColor.toColor(): ComposeColor = basicColors[this]!!

        val winningColors = usedColorsToCellColors.map { (composeColor, _) -> composeColor } + Yellow + White
        private val basicColors = usedColorsToCellColors.filter { (_, cellColors) -> cellColors.size == 1 }
            .associate { (composeColor, cellColors) -> cellColors.first() to composeColor }
    }
}
