package com.bosha.pomodoro.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bosha.pomodoro.StopwatchListener
import com.bosha.pomodoro.data.entity.Stopwatch
import com.bosha.pomodoro.utils.UNIT_TEN_MS
import com.bosha.pomodoro.utils.tickerFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlin.reflect.KFunction1
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
class DashboardViewModel : ViewModel() {

    private val stopwatches = mutableListOf<Stopwatch>()
    private var doOnTick: (() -> Unit)? = null
    private var setIsRecyclableHolder: KFunction1<Boolean, Unit>? = null
    private val _sharedFlow = MutableSharedFlow<List<Stopwatch>>(1,10)
    val sharedFlow = _sharedFlow.asSharedFlow()
    var nextId = 0

    val timer = tickerFlow(Duration.Companion.milliseconds(UNIT_TEN_MS))
        .onEach { doOnTick?.invoke() }
        .launchIn(viewModelScope)


    val listener: StopwatchListener = object : StopwatchListener {

        override fun start(id: Int) {
            clearStartedTimers()
            changeStopwatch(id, null, true)
        }

        override fun stop(id: Int, currentMs: Long) {
            changeStopwatch(id, currentMs, false)
        }

        override fun reset(id: Int) {
            val index = stopwatches.indexOfFirst { it.id == id }
            changeStopwatch(id, stopwatches[index].beginTime, false)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun delete(id: Int) {
            setIsRecyclableHolder?.invoke(true)
            stopwatches.removeIf { it.id == id }
            _sharedFlow.tryEmit(stopwatches.toList())
        }

        override fun setFinish(id: Int) {
            changeStopwatch(id, 0, isStarted = false, isFinish = true)
        }

        override fun setOnTickListener(recycleHolder: KFunction1<Boolean, Unit>, block: (() -> Unit)?) {
            doOnTick = block
            setIsRecyclableHolder = recycleHolder
        }

        private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean, isFinish: Boolean = false) {
            setIsRecyclableHolder?.invoke(true)
            val index = stopwatches.indexOfFirst { it.id == id }
            if (index != -1) stopwatches[index].also { stopwatch ->
                stopwatches[index] = stopwatches[index]
                        .copy(
                            id = stopwatch.id,
                            untilFinishMs = currentMs ?: stopwatch.untilFinishMs,
                            isStarted = isStarted,
                            isFinished = isFinish
                        )
            }
            _sharedFlow.tryEmit(stopwatches.toList())
        }

        private fun clearStartedTimers(){
            val index = stopwatches.indexOfFirst { it.isStarted }
            if (index != -1) stopwatches[index].also {
                stopwatches[index] = stopwatches[index]
                    .copy(it.id, it.untilFinishMs, isStarted = false)


            }
        }

    }

    fun addStopwatch(stopwatch: Stopwatch) {
        stopwatches.add(stopwatch)
        _sharedFlow.tryEmit(stopwatches.toList())
    }

}