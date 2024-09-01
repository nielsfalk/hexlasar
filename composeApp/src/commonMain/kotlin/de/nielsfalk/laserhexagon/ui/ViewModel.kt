package de.nielsfalk.laserhexagon.ui

abstract class ViewModel<S, E>(
    val state: S,
    private val setState: (S) -> Unit
) {
    protected fun S.update(function: (S) -> S) {
        setState(function(this))
    }

    abstract fun onEvent(event: E): Unit
}