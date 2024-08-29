package de.nielsfalk.laserhexagon

import kotlin.random.Random


fun Float.roundUp(): Int =
    (this * 100).toInt().let {
        it / 100 + if (it % 100 == 0) 0 else 1
    }

class ControlledRandom(givenInts: List<Int>) : Random() {
    private val givenIntsIterator = givenInts.iterator()
    override fun nextBits(bitCount: Int): Int {
        throw NotImplementedError()
    }

    override fun nextInt(until: Int): Int =
        nextInt()

    override fun nextInt(): Int =
        givenIntsIterator.next()
}

fun randomExecution(random: Random, function: PercentContextBuilder.() -> Unit) {
    val parts = PercentContextBuilder().apply(function).parts.iterator()
    var random = random.nextInt(100)
    while (parts.hasNext()) {
        val (odds, oddFunction) = parts.next()
        if (random < odds) {
            oddFunction()
            return
        }
        random -= odds
    }
}

data class PercentContextBuilder(val parts: MutableList<Pair<Int, () -> Unit>> = mutableListOf()) {
    infix fun Int.`percent do`(function: () -> Unit) {
        parts.add(this to function)
    }
}