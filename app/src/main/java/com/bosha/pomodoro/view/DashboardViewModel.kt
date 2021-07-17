package com.bosha.pomodoro.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bosha.pomodoro.StopwatchListener
import com.bosha.pomodoro.data.entity.Stopwatch
import com.bosha.pomodoro.utils.tickerFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
class DashboardViewModel : ViewModel() {

    private val stopwatches = mutableListOf<Stopwatch>()
    val sharedFlow = MutableSharedFlow<List<Stopwatch>>(1)
    var nextId = 0
    private var doOnTick: (() -> Unit)? = null

    val timer = tickerFlow(Duration.Companion.milliseconds(UNIT_TEN_MS))
        .onEach { doOnTick?.invoke() }
        .launchIn(viewModelScope)


    val listener: StopwatchListener = object : StopwatchListener {

        override fun start(id: Int) {
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
            stopwatches.removeIf { it.id == id }
            sharedFlow.tryEmit(stopwatches.toList())
        }

        override fun setOnTickListener(block: (() -> Unit)?) {
            doOnTick = block
        }

        private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean) {

            var index = stopwatches.indexOfFirst { it.isStarted }
            if (index != -1) stopwatches[index].also {
                stopwatches[index] = Stopwatch(it.id, it.currentMs, false)
            }



             index = stopwatches.indexOfFirst { it.id == id }
            if (index != -1) stopwatches[index].also {
                stopwatches[index] = Stopwatch(it.id, currentMs ?: it.currentMs, isStarted)
            }


            sharedFlow.tryEmit(stopwatches.toList())
        }

    }

    fun addStopwatch(stopwatch: Stopwatch) {
        stopwatches.add(stopwatch)
        sharedFlow.tryEmit(stopwatches.toList())
    }

    private companion object {
        private const val UNIT_TEN_MS = 10
    }

}