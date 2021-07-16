package com.bosha.pomodoro.view.adapter

import android.graphics.drawable.AnimationDrawable
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.bosha.pomodoro.R
import com.bosha.pomodoro.StopwatchListener
import com.bosha.pomodoro.data.entity.Stopwatch
import com.bosha.pomodoro.databinding.RecyclerTimerItemBinding
import com.bosha.pomodoro.utils.tickerFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


@ExperimentalTime
class StopwatchViewHolder(
    private val binding: RecyclerTimerItemBinding,
    private val listener: StopwatchListener
) : RecyclerView.ViewHolder(binding.root) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var timer: Job? = null

    fun bind(stopwatch: Stopwatch) {
        binding.tvTimer.text = stopwatch.currentMs.time()
        if (stopwatch.isStarted) {
            startTimer(stopwatch)
        } else {
            stopTimer()
        }

        initListeners(stopwatch)
    }

    private fun initListeners(stopwatch: Stopwatch) {
        binding.ibStart.setOnClickListener {
            if (stopwatch.isStarted) {
                timer?.cancel()
                listener.stop(stopwatch.id, stopwatch.currentMs)
            } else {
                listener.start(stopwatch.id)
            }
        }

        binding.ibCancel.setOnClickListener {
            timer?.cancel()
            listener.reset(stopwatch.id)
        }

        binding.ibDeleteButton.setOnClickListener {
            timer?.cancel()
            listener.delete(stopwatch.id) }
    }

    private fun startTimer(stopwatch: Stopwatch) {
        val image = ContextCompat.getDrawable(binding.root.context, R.drawable.ic_pause)
        binding.ibStart.setImageDrawable(image)

        timer?.cancel()
        timer = tickerFlow(Duration.Companion.milliseconds(UNIT_TEN_MS))
            .onEach {
                binding.tvTimer.post {
                    stopwatch.currentMs += UNIT_TEN_MS
                    binding.tvTimer.text = stopwatch.currentMs.time()
                }
            }
            .launchIn(scope)

        binding.ivAnimatedPoint.isInvisible = false
        (binding.ivAnimatedPoint.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer() {
        timer?.cancel()
        val image = ContextCompat.getDrawable(binding.root.context, R.drawable.ic_arrow)
        binding.ibStart.setImageDrawable(image)



        binding.ivAnimatedPoint.isInvisible = true
        (binding.ivAnimatedPoint.background as? AnimationDrawable)?.stop()
    }




    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else {
            "0$count"
        }
    }

    private fun Long.time(): String {
        if (this <= 0L) {
            return START_TIME
        }
        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60
        val ms = this % 1000 / 10

        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}:${displaySlot(ms)}"
    }


    private companion object {

        private const val START_TIME = "00:00:00:00"
        private const val UNIT_TEN_MS = 10
    }

}


