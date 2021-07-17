package com.bosha.pomodoro.view

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bosha.pomodoro.R
import com.bosha.pomodoro.StopwatchListener
import com.bosha.pomodoro.data.entity.Stopwatch
import com.bosha.pomodoro.databinding.DashboardFragmentBinding
import com.bosha.pomodoro.view.adapter.StopwatchAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.ExperimentalTime
@ExperimentalTime
class DashboardFragment : Fragment(R.layout.dashboard_fragment) {

    private val viewModel: DashboardViewModel by viewModels()

    private var _binding: DashboardFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)
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
                viewModel.addStopwatch(Stopwatch(viewModel.nextId++,0,false))
            }
        }

        viewModel.sharedFlow
            .onEach(::handleChanges)
            .launchIn(lifecycleScope)
    }

    private fun handleChanges(list: List<Stopwatch>) =
        stopwatchAdapter.submitList(list)

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
