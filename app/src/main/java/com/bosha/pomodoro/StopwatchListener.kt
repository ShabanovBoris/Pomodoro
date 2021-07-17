package com.bosha.pomodoro

import kotlin.reflect.KFunction1
import kotlin.time.ExperimentalTime


interface StopwatchListener {

    fun start(id: Int)

    fun stop(id: Int, currentMs: Long)

    fun reset(id: Int)

    fun delete(id: Int)

    @ExperimentalTime
    fun setOnTickListener(retainHolder: KFunction1<Boolean, Unit>, block: (() -> Unit)?)
}