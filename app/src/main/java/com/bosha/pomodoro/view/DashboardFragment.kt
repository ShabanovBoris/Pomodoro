package com.bosha.pomodoro.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.bosha.pomodoro.DialogHolder
import com.bosha.pomodoro.R
import com.bosha.pomodoro.data.entity.Stopwatch
import com.bosha.pomodoro.data.entity.time
import com.bosha.pomodoro.databinding.DashboardFragmentBinding
import com.bosha.pomodoro.utils.*
import com.bosha.pomodoro.view.adapter.StopwatchAdapter
import com.bosha.pomodoro.view.services.TimerForegroundService
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.ExperimentalTime

@ExperimentalTime
class DashboardFragment : Fragment(R.layout.dashboard_fragment), DialogHolder, LifecycleObserver {

    private val viewModel: DashboardViewModel by viewModels()
    private var applicationContext: Context? = null
    private var freshStopwatchList = emptyList<Stopwatch>()
    private var _binding: DashboardFragmentBinding? = null
    private val stopwatchAdapter by lazy(LazyThreadSafetyMode.NONE)
    { StopwatchAdapter(viewModel.listener) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applicationContext = requireActivity().applicationContext
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        setBackPressDispatcher()
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
                "Timer with time ${result.stopwatch.beginTime.time(isStart = true)} has finished",
                Toast.LENGTH_SHORT
            ).show()
            is DashboardViewModel.TimerResult.UpdateList -> {
                stopwatchAdapter.submitList(result.list)
                freshStopwatchList = result.list
            }
        }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }

    override fun perform(value: Long) =
        viewModel.addStopwatch(value)


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStopProcess() {
        Log.e("LIFECYCLE", "onStopProcess: ")
        val startedStopwatch = freshStopwatchList.getStartedOrNull() ?: return
        val intent = Intent(applicationContext, TimerForegroundService::class.java).apply {
                putExtra(COMMAND_ID, COMMAND_START)
                putExtra(BEGIN_TIME_MS, startedStopwatch.beginTime)
                putExtra(UNTIL_FINISH_MS, startedStopwatch.untilFinishMs)
            }
        ContextCompat.startForegroundService(
            requireNotNull(applicationContext), intent )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStartProcess() {
        Log.e("LIFECYCLE", "onStartProcess: ")
        val stopIntent = Intent(applicationContext, TimerForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        applicationContext?.startService(stopIntent)
    }


    private fun setBackPressDispatcher(){
            requireActivity().onBackPressedDispatcher.addCallback {
                if(freshStopwatchList.getStartedOrNull() == null) requireActivity().finish()
                else requireActivity().startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME))
            }
    }
}

