package com.bosha.pomodoro.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bosha.pomodoro.StopwatchListener
import com.bosha.pomodoro.data.entity.Stopwatch
import com.bosha.pomodoro.utils.tickerFlow
import com.bosha.pomodoro.view.adapter.StopwatchViewHolder
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KFunction1
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
class DashboardViewModel : ViewModel() {

    private val stopwatches = mutableListOf<Stopwatch>()
    private var doOnTick: (() -> Unit)? = null
    private var setIsRecyclableHolder: KFunction1<Boolean, Unit>? = null
    private val _sharedFlow = MutableSharedFlow<List<Stopwatch>>(1)
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
            changeStopwatch(id, 0, false)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun delete(id: Int) {
            setIsRecyclableHolder?.invoke(true)
            stopwatches.removeIf { it.id == id }
            _sharedFlow.tryEmit(stopwatches.toList())
        }

        override fun setOnTickListener(recyclableHolder: KFunction1<Boolean, Unit>, block: (() -> Unit)?) {
            doOnTick = block
            setIsRecyclableHolder = recyclableHolder
        }

        private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean) {
            setIsRecyclableHolder?.invoke(true)
            val index = stopwatches.indexOfFirst { it.id == id }
            if (index != -1) stopwatches[index].also {
                stopwatches[index] = Stopwatch(it.id, currentMs ?: it.currentMs, isStarted)
            }
            _sharedFlow.tryEmit(stopwatches.toList())
        }

        private fun clearStartedTimers(){
            val index = stopwatches.indexOfFirst { it.isStarted }
            if (index != -1) stopwatches[index].also {
                stopwatches[index] = Stopwatch(it.id, it.currentMs, false)
            }
        }

    }

    fun addStopwatch(stopwatch: Stopwatch) {
        stopwatches.add(stopwatch)
        _sharedFlow.tryEmit(stopwatches.toList())
    }

    private companion object {
        private const val UNIT_TEN_MS = 10
    }

}