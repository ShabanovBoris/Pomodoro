package com.bosha.pomodoro.utils

import com.bosha.pomodoro.data.entity.Stopwatch

fun List<Stopwatch>.findStopwatchById(id:Int): Stopwatch {
    val index = this.indexOfFirst { it.id == id }
    if (index == -1) error("Stopwatch doesn't exist")
    return this[index]
}

