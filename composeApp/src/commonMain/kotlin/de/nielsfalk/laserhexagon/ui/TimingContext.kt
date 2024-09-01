package de.nielsfalk.laserhexagon.ui

import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

data class TimingContext(
    val start: Long,
    val iterationStart: Long,
    val lastIterationStart: Long,
) {
    val delta: Int by lazy { (iterationStart - lastIterationStart).toInt() }
    val spendTime: Int by lazy { (iterationStart - start).toInt() }

    companion object {
        fun currentMillis(): Long =
            Clock.System.now().toEpochMilliseconds()

        suspend fun repeatWithTiming(function: suspend TimingContext.() -> Boolean) {
            val start = currentMillis()
            var lastIterationStart = start
            do {
                val iterationStart = currentMillis()
                val running = TimingContext(
                    start = start,
                    iterationStart = iterationStart,
                    lastIterationStart = lastIterationStart
                ).function()
                lastIterationStart = iterationStart
                delay(1)
            } while (running)
        }
    }
}