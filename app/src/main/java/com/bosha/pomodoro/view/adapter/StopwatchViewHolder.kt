package com.bosha.pomodoro.view.adapter

import android.graphics.drawable.AnimationDrawable
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.bosha.pomodoro.R
import com.bosha.pomodoro.StopwatchListener
import com.bosha.pomodoro.data.entity.Stopwatch
import com.bosha.pomodoro.data.entity.time
import com.bosha.pomodoro.databinding.RecyclerTimerItemBinding
import com.google.android.material.button.MaterialButton
import kotlin.time.ExperimentalTime

@ExperimentalTime
class StopwatchViewHolder(
    private val binding: RecyclerTimerItemBinding,
    private val listener: StopwatchListener
) : RecyclerView.ViewHolder(binding.root) {

    private var timerStartTime = 0L
    var isActive = false

    fun bind(stopwatch: Stopwatch) {
        if (stopwatch.untilFinishMs == stopwatch.beginTime)
        binding.tvTimer.text = stopwatch.untilFinishMs.time(true)
        else binding.tvTimer.text = stopwatch.untilFinishMs.time()

        if (stopwatch.isStarted) {
            startTimer(stopwatch)
        } else {
            stopTimer(stopwatch.isFinished)
        }
        initListeners(stopwatch)

        binding.cpbTimer.setPeriod(stopwatch.beginTime)
        binding.cpbTimer.setCurrent(stopwatch.untilFinishMs)
    }

    private fun initListeners(stopwatch: Stopwatch) {
        binding.ibStart.setOnClickListener {
            if (stopwatch.isStarted) {
                stopTimer(stopwatch.isFinished)
                listener.stop(stopwatch.id, stopwatch.untilFinishMs)
            } else {
                listener.start(stopwatch.id)
            }
        }
        binding.ibCancel.setOnClickListener {
            stopTimer()
            listener.reset(stopwatch.id)
        }
        binding.ibDeleteButton.setOnClickListener {
            listener.delete(stopwatch.id)
        }
    }

    private fun startTimer(stopwatch: Stopwatch) {
        isActive = true
        timerStartTime = System.currentTimeMillis()
        if(adapterPosition == layoutPosition)
        this.setIsRecyclable(false)

        val image = ContextCompat.getDrawable(binding.root.context, R.drawable.ic_pause)
        binding.ibStart.apply {
            icon = image
            iconGravity = MaterialButton.ICON_GRAVITY_END
            text = context.getString(R.string.stop_text_button)
        }
        binding.ivAnimatedPoint.isInvisible = false
        (binding.ivAnimatedPoint.background as? AnimationDrawable)?.start()


        val savedValue = stopwatch.beginTime - stopwatch.untilFinishMs
        listener.setOnTickListener(::setIsRecyclable){
            if (isActive) {
                stopwatch.untilFinishMs =
                    (stopwatch.beginTime - savedValue)  - (System.currentTimeMillis() - timerStartTime)
                if (stopwatch.untilFinishMs <= 0L) {
                    stopTimer()
                    listener.setFinish(stopwatch.id)
                }
                binding.tvTimer.text = stopwatch.untilFinishMs.time()
                binding.cpbTimer.setCurrent(stopwatch.untilFinishMs)
            }
        }
    }


    private fun stopTimer(isFinish: Boolean = false) {
        isActive = false
        val image = ContextCompat.getDrawable(binding.root.context, R.drawable.ic_arrow)
        binding.ibStart.apply {
            icon = image
            iconGravity = MaterialButton.ICON_GRAVITY_END
            text = context.getString(R.string.start_text_button)
        }

        binding.ivAnimatedPoint.isInvisible = true
        (binding.ivAnimatedPoint.background as? AnimationDrawable)?.stop()

        if (isFinish) {
            val typedValue = TypedValue()
            binding.root.context.theme
                .resolveAttribute(R.attr.colorSecondaryVariant,typedValue,true)
            binding.cvTimerCardView.setCardBackgroundColor(typedValue.data)

            binding.tvTimer.text = binding.root.context.getString(R.string.timer_string)

            val greyFilter = ContextCompat.getColor(binding.root.context,R.color.material_on_background_disabled)
            binding.ibStart.isEnabled = false
            binding.ibCancel.isEnabled = false
            binding.ibCancel.setColorFilter(greyFilter)
        }
    }
}


