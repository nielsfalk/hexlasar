package de.nielsfalk.util

import androidx.compose.ui.graphics.drawscope.DrawScope

fun DrawScope.layers(function: LayerDrawScope.() -> Unit) {
    val layerFutures: MutableList<Pair<Int, () -> Unit>> = mutableListOf()
    LayerDrawScope(this) { layer, function ->
        layerFutures += layer to function
    }.function()
    layerFutures.sortedBy { (layer, _) -> layer }
        .forEach { (_, future) -> future() }
}

open class LayerDrawScope(
    private val wrapped: DrawScope,
    private val setOnLayer: (Int, () -> Unit) -> Unit,
) : DrawScope by wrapped {
    constructor(wrapped:LayerDrawScope) : this(wrapped.wrapped, wrapped.setOnLayer)

    fun onLayer(layer: Int, function: () -> Unit) {
        setOnLayer(layer, function)
    }
}