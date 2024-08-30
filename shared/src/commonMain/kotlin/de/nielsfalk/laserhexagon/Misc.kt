package de.nielsfalk.laserhexagon

import kotlin.random.Random


fun Float.roundUp(): Int {
    return (this * 1000).toInt().let {
        it / 1000 + if (it % 1000 == 0) 0 else 1
    }
}

class ControlledRandom(givenInts: List<Int>) : Random() {
    constructor(vararg givenInts: Int) : this(givenInts.toList())

    private val givenIntsIterator = givenInts.iterator()
    override fun nextBits(bitCount: Int): Int {
        throw NotImplementedError()
    }

    override fun nextInt(until: Int): Int =
        nextInt()

    override fun nextInt(): Int =
        givenIntsIterator.next()
}

fun <T> randomExecution(random: Random, function: PercentContextBuilder<T>.() -> Unit):T {
    val parts = PercentContextBuilder<T>().apply { function() }.parts.iterator()
    var nextInd = random.nextInt(100)
    while (parts.hasNext()) {
        val (odds, oddFunction) = parts.next()
        if (nextInd < odds) {
            return oddFunction()
        }
        nextInd -= odds
    }
    throw IllegalArgumentException("all ods should be defined")
}

data class PercentContextBuilder<T>(val parts: MutableList<Pair<Int, () -> T>> = mutableListOf()) {
    infix fun Int.percentDo(function: () -> T) {
        parts.add(this to function)
    }

    fun elseDo(function: () -> T) {
        100 percentDo function
    }
}

val Int.odd: Boolean get() = this % 2 != 0
val Int.even: Boolean get() = this % 2 != 1
