package com.bosha.pomodoro.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bosha.pomodoro.StopwatchListener
import com.bosha.pomodoro.data.entity.Stopwatch
import com.bosha.pomodoro.utils.UNIT_TEN_MS
import com.bosha.pomodoro.utils.findStopwatchById
import com.bosha.pomodoro.utils.tickerFlow
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
    private var nextId = 0
    private var doOnTick: (() -> Unit)? = null
    private var setIsRecyclableHolder: KFunction1<Boolean, Unit>? = null
    private val _sharedFlow = MutableSharedFlow<TimerResult>(1, 10)
    val sharedFlow = _sharedFlow.asSharedFlow()

    init {
        tickerFlow(Duration.Companion.milliseconds(UNIT_TEN_MS))
            .onEach { doOnTick?.invoke() }
            .launchIn(viewModelScope)
    }


    val listener: StopwatchListener = object : StopwatchListener {

        override fun start(id: Int) {
            clearStartedTimers()
            changeStopwatch(id, null, true)
        }

        override fun stop(id: Int, currentMs: Long) {
            changeStopwatch(id, currentMs, false)
        }

        override fun reset(id: Int) {
            changeStopwatch(id, stopwatches.findStopwatchById(id).beginTime, false)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun delete(id: Int) {
            setIsRecyclableHolder?.invoke(true)
            stopwatches.removeIf { it.id == id }
            _sharedFlow.tryEmit(TimerResult.UpdateList(stopwatches.toList()))
        }

        override fun setFinish(id: Int) {
            _sharedFlow.tryEmit(
                TimerResult.FinishResult(stopwatches.findStopwatchById(id))
            )
            changeStopwatch(id, 0, isStarted = false, isFinish = true)

        }

        override fun setOnTickListener(
            recycleHolder: KFunction1<Boolean, Unit>,
            block: (() -> Unit)?
        ) {
            doOnTick = block
            setIsRecyclableHolder = recycleHolder
        }

        private fun changeStopwatch(
            id: Int,
            currentMs: Long?,
            isStarted: Boolean,
            isFinish: Boolean = false
        ) {
            setIsRecyclableHolder?.invoke(true)
            val index = stopwatches.indexOfFirst { it.id == id }
            if (index != -1) stopwatches[index] = stopwatches[index]
                    .copy(
                        untilFinishMs = currentMs ?: stopwatches[index].untilFinishMs,
                        isStarted = isStarted,
                        isFinished = isFinish
                    )
            _sharedFlow.tryEmit(TimerResult.UpdateList(stopwatches.toList()))
        }

        private fun clearStartedTimers() {
            val index = stopwatches.indexOfFirst { it.isStarted }
            if (index != -1) stopwatches[index] = stopwatches[index].copy(isStarted = false)
        }

    }

    fun addStopwatch(time: Long) {
        stopwatches.add(Stopwatch(nextId++, time, time, false))
        _sharedFlow.tryEmit(TimerResult.UpdateList(stopwatches.toList()))
    }

    sealed class TimerResult {
        data class UpdateList(val list: List<Stopwatch>) : TimerResult()
        data class FinishResult(val stopwatch: Stopwatch) : TimerResult()
    }
}