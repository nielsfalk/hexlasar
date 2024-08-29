package de.nielsfalk.laserhexagon


fun Float.roundUp(): Int =
    (this * 100).toInt().let {
        it / 100 + if (it % 100 == 0) 0 else 1
    }