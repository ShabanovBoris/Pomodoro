package com.bosha.pomodoro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.bosha.pomodoro.view.DashboardFragment
import kotlin.time.ExperimentalTime

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            supportFragmentManager.commit {
                replace<DashboardFragment>(R.id.main_container)
            }
    }
}