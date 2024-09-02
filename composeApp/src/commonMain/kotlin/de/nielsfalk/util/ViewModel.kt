package de.nielsfalk.util

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope

abstract class ViewModel<STATE, EVENT> {

    private lateinit var getstate: () -> STATE
    private lateinit var setState: (STATE) -> Unit
    private lateinit var _viewModelScope: CoroutineScope

    val state get(): STATE = getstate()
    val viewModelScope get() = _viewModelScope

    protected fun STATE.update(function: (STATE) -> STATE) {
        setState(function(this))
    }

    abstract fun onEvent(event: EVENT): Unit

    companion object {
        @Composable
        fun <STATE, EVENT, VIEWMODEL : ViewModel<STATE, EVENT>> getViewModel(
            factoryMethod: () -> VIEWMODEL,
            initialState: STATE,
        ): VIEWMODEL {
            var state: STATE by remember { mutableStateOf(initialState) }
            val viewModelScope = rememberCoroutineScope()
            return factoryMethod().also {
                it.getstate = { state }
                it.setState = { state = it }
                it._viewModelScope = viewModelScope
            }
        }
    }
}
