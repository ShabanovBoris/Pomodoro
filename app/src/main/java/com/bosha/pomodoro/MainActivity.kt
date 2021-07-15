package com.bosha.pomodoro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState.init()
    }

    private fun Bundle?.init() {
        if (this == null) {
            supportFragmentManager.commit {
                replace<DashboardFragment>(R.id.main_container)
            }
        }
    }
}