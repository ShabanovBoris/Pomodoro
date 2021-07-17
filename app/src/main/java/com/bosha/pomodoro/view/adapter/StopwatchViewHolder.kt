package com.bosha.pomodoro.view.adapter

import android.graphics.drawable.AnimationDrawable
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.bosha.pomodoro.R
import com.bosha.pomodoro.StopwatchListener
import com.bosha.pomodoro.data.entity.Stopwatch
import com.bosha.pomodoro.data.entity.time
import com.bosha.pomodoro.databinding.RecyclerTimerItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlin.time.ExperimentalTime


@ExperimentalTime
class StopwatchViewHolder(
    private val binding: RecyclerTimerItemBinding,
    private val listener: StopwatchListener
) : RecyclerView.ViewHolder(binding.root) {

    var isActive = false

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
                stopTimer()
                listener.stop(stopwatch.id, stopwatch.currentMs)
            } else {
                listener.start(stopwatch.id)
            }
        }

        binding.ibCancel.setOnClickListener {
            listener.reset(stopwatch.id)
        }

        binding.ibDeleteButton.setOnClickListener {
            listener.delete(stopwatch.id)
        }
    }

    private fun startTimer(stopwatch: Stopwatch) {
        isActive = true
        val image = ContextCompat.getDrawable(binding.root.context, R.drawable.ic_pause)
        binding.ibStart.setImageDrawable(image)

        binding.ivAnimatedPoint.isInvisible = false
        (binding.ivAnimatedPoint.background as? AnimationDrawable)?.start()

        listener.setOnTickListener{
            if (isActive) {
                stopwatch.currentMs += UNIT_TEN_MS
                binding.tvTimer.text = stopwatch.currentMs.time()
            }
        }
    }

    private fun stopTimer() {
        isActive = false
        val image = ContextCompat.getDrawable(binding.root.context, R.drawable.ic_arrow)
        binding.ibStart.setImageDrawable(image)

        binding.ivAnimatedPoint.isInvisible = true
        (binding.ivAnimatedPoint.background as? AnimationDrawable)?.stop()
    }

    companion object {
        const val START_TIME = "00:00:00:00"
        private const val UNIT_TEN_MS = 10
    }
}


