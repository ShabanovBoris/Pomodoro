package com.bosha.pomodoro.data.entity

import com.bosha.pomodoro.view.adapter.StopwatchViewHolder
import kotlin.time.ExperimentalTime

data class Stopwatch(
    val id: Int,
    var currentMs: Long,
    var isStarted: Boolean
)

@ExperimentalTime
 fun Long.time(): String {
    if (this <= 0L) {
        return StopwatchViewHolder.START_TIME
    }
    val h = this / 1000 / 3600
    val m = this / 1000 % 3600 / 60
    val s = this / 1000 % 60
    val ms = this % 1000 / 10

    return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}:${displaySlot(ms)}"
}

private fun displaySlot(count: Long): String {
    return if (count / 10L > 0) {
        "$count"
    } else {
        "0$count"
    }
}




