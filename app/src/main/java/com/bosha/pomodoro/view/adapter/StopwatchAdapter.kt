package com.bosha.pomodoro.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bosha.pomodoro.StopwatchListener
import com.bosha.pomodoro.data.entity.Stopwatch
import com.bosha.pomodoro.databinding.RecyclerTimerItemBinding
import kotlin.time.ExperimentalTime

@ExperimentalTime
class StopwatchAdapter(private val stopwatchListener: StopwatchListener) :
    ListAdapter<Stopwatch, StopwatchViewHolder>(diffUtil) {


    // If holder with stopwatch that was finished, create holder with new viewType
    override fun getItemViewType(position: Int): Int =
        if (getItem(position).isFinished) 0 else 1


    private companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<Stopwatch>() {

            override fun areItemsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean =
                oldItem.id == newItem.id

            override fun getChangePayload(oldItem: Stopwatch, newItem: Stopwatch) = Any()

            override fun areContentsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean =
                oldItem == newItem
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchViewHolder =
        StopwatchViewHolder(
            RecyclerTimerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            stopwatchListener
        )

    override fun onBindViewHolder(holder: StopwatchViewHolder, position: Int) =
        holder.bind(getItem(position))
}


