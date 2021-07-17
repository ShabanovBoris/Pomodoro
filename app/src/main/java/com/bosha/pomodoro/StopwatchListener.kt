package com.bosha.pomodoro

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow


interface StopwatchListener {

    fun start(id: Int)

    fun stop(id: Int, currentMs: Long)

    fun reset(id: Int)

    fun delete(id: Int)

    fun setOnTickListener(block: (() -> Unit)?)
}