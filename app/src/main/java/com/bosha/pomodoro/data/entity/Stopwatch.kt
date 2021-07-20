package com.bosha.pomodoro.data.entity

import kotlin.time.ExperimentalTime

data class Stopwatch(
    val id: Int,
    var untilFinishMs: Long,
    val beginTime: Long ,
    var isStarted: Boolean,
    var isFinished: Boolean = false
)
@ExperimentalTime
fun Long.time(isStart: Boolean = false): String {

    val h = this / 1000 / 3600
    val m = this / 1000 % 3600 / 60
    val s = this / 1000 % 60

     if (isStart)
         return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
    else {
         if (s + 1L != 60L) return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s + 1)}"
         if (m + 1L != 60L) return "${displaySlot(h)}:${displaySlot(m+1)}:00"
         else return "${displaySlot(h+1)}:00:00"
    }

}

private fun displaySlot(count: Long): String {
    return if (count / 10L > 0) {
        "$count"
    } else {
        "0$count"
    }

}




