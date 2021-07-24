package com.bosha.pomodoro.utils

import com.bosha.pomodoro.data.entity.Stopwatch

const val INVALID = "INVALID"
const val COMMAND_START = "COMMAND_START"
const val COMMAND_STOP = "COMMAND_STOP"
const val COMMAND_ID = "COMMAND_ID"
const val BEGIN_TIME_MS = "STARTED_TIMER_TIME"
const val UNTIL_FINISH_MS = "UNTIL_FINISH_TIME"

fun List<Stopwatch>.findStopwatchById(id:Int): Stopwatch {
    val index = this.indexOfFirst { it.id == id }
    if (index == -1) error("Stopwatch doesn't exist")
    return this[index]
}

fun List<Stopwatch>.getStartedOrNull(): Stopwatch? {
    return this.find { it.isStarted }
}