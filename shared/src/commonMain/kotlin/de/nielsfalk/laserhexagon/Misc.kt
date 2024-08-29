package de.nielsfalk.laserhexagon

import kotlin.random.Random


fun Float.roundUp(): Int {
    return (this * 1000).toInt().let {
        it / 1000 + if (it % 1000 == 0) 0 else 1
    }
}

class ControlledRandom(givenInts: List<Int>) : Random() {
    constructor(vararg givenInts:Int) : this(givenInts.toList())

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

val Int.odd: Boolean get() = this % 2 != 0
val Int.even: Boolean get() = this % 2 != 1
