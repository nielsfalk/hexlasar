package de.nielsfalk.laserhexagon.ui

import androidx.compose.ui.graphics.Color as ComposeColor
import de.nielsfalk.laserhexagon.Color as CellColor

class Color {
    companion object {
        val White = ComposeColor.White
        val Green = ComposeColor.Green
        val Yellow = ComposeColor(0xfffff302)
        val Orange = ComposeColor(0xfff88f09)
        val Red = ComposeColor(0xffe10056)
        val Purple = ComposeColor(0xffa818cc)
        val Blue = ComposeColor(0xff0270dd)
        val Black = ComposeColor.Black

        private val usedColorsToCellColors: List<Pair<ComposeColor, Set<CellColor>>> = listOf(
            White to setOf(CellColor.Orange, CellColor.Green, CellColor.Purple),
            Green to setOf(CellColor.Green),
            Yellow to setOf(CellColor.Green, CellColor.Orange),
            Orange to setOf(CellColor.Orange),
            Red to setOf(CellColor.Orange, CellColor.Purple),
            Purple to setOf(CellColor.Purple),
            Blue to setOf(CellColor.Purple, CellColor.Green)
        )

        fun Set<CellColor>.toColor(): ComposeColor? =
            usedColorsToCellColors.firstOrNull() { (_, cellColor) -> cellColor == this }?.first

        fun CellColor.toColor(): ComposeColor = basicColors[this]!!

        val winningColors = usedColorsToCellColors.map { (composeColor, _) -> composeColor } + Green + White
        private val basicColors = usedColorsToCellColors.filter { (_, cellColors) -> cellColors.size == 1 }
            .associate { (composeColor, cellColors) -> cellColors.first() to composeColor }
    }
}
