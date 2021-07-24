package com.bosha.pomodoro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.*
import com.bosha.pomodoro.utils.*
import com.bosha.pomodoro.view.DashboardFragment
import com.bosha.pomodoro.view.DashboardViewModel
import com.bosha.pomodoro.view.services.TimerForegroundService
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MainActivity : AppCompatActivity(R.layout.activity_main){

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            supportFragmentManager.commit {
                replace<DashboardFragment>(R.id.main_container)
            }
    }
}