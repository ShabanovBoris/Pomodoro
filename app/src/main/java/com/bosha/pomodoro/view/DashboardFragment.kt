package com.bosha.pomodoro.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bosha.pomodoro.DialogHolder
import com.bosha.pomodoro.R
import com.bosha.pomodoro.data.entity.Stopwatch
import com.bosha.pomodoro.data.entity.time
import com.bosha.pomodoro.databinding.DashboardFragmentBinding
import com.bosha.pomodoro.view.adapter.StopwatchAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.ExperimentalTime

@ExperimentalTime
class DashboardFragment : Fragment(R.layout.dashboard_fragment), DialogHolder {

    private val viewModel: DashboardViewModel by viewModels()

    private var _binding: DashboardFragmentBinding? = null
    private val stopwatchAdapter by lazy(LazyThreadSafetyMode.NONE)
    { StopwatchAdapter(viewModel.listener) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = DashboardFragmentBinding.bind(view).apply {
            rvStopwatchList.run {
                layoutManager = LinearLayoutManager(requireContext())
                hasFixedSize()
                adapter = stopwatchAdapter
            }

            fabAddTimer.setOnClickListener {
                TimePickerBottomSheet().show(childFragmentManager, null)
            }
        }

        viewModel.sharedFlow
            .onEach(::handleChanges)
            .launchIn(lifecycleScope)
    }


    private fun handleChanges(result: DashboardViewModel.TimerResult) =
        when (result) {
            is DashboardViewModel.TimerResult.FinishResult -> Toast.makeText(
                requireContext(),
                "Timer with time ${result.stopwatch.beginTime.time(isStart = true)} has done",
                Toast.LENGTH_SHORT
            ).show()
            is DashboardViewModel.TimerResult.UpdateList -> stopwatchAdapter.submitList(result.list)
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun perform(value: Long) =
        viewModel.addStopwatch(value)

}

