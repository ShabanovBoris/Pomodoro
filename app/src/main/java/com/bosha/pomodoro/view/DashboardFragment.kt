package com.bosha.pomodoro.view

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bosha.pomodoro.R
import com.bosha.pomodoro.StopwatchListener
import com.bosha.pomodoro.data.entity.Stopwatch
import com.bosha.pomodoro.databinding.DashboardFragmentBinding
import com.bosha.pomodoro.view.adapter.StopwatchAdapter
import kotlin.time.ExperimentalTime
@ExperimentalTime
class DashboardFragment : Fragment(R.layout.dashboard_fragment), StopwatchListener {

    private val viewModel: DashboardViewModel by viewModels()
    private var _binding: DashboardFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)


    private val stopwatchAdapter by lazy(LazyThreadSafetyMode.NONE)
    { StopwatchAdapter(this)}

    private val mStopwatches = mutableListOf<Stopwatch>()
    private var nextId = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DashboardFragmentBinding.bind(view).apply {
            rvStopwatchList.run {
                layoutManager = LinearLayoutManager(requireContext())
                hasFixedSize()
                adapter = stopwatchAdapter
            }
            fabAddTimer.setOnClickListener {
                mStopwatches.add(Stopwatch(nextId++,0,false))
                stopwatchAdapter.submitList(mStopwatches.toList())
            }

        }

    }

    override fun start(id: Int) {
       changeStopwatch(id,null,true)
    }

    override fun stop(id: Int, currentMs: Long) {
        changeStopwatch(id, currentMs, false)
    }

    override fun reset(id: Int) {
        changeStopwatch(id, 0, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun delete(id: Int) {
        mStopwatches.removeIf { it.id == id }
        stopwatchAdapter.submitList(mStopwatches.toList())
    }

    private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean) {
        val index = mStopwatches.indexOfFirst { it.id == id }
        if (index != -1)  mStopwatches[index].also {
            mStopwatches[index] = Stopwatch(it.id, currentMs ?: it.currentMs, isStarted)
        }
        stopwatchAdapter.submitList(mStopwatches.toList())
    }
}
