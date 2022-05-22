package com.xjl.gdinject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val thread = MainRealDemoThread()
        thread.test()
        thread.test1()
    }


    fun innerTestTryCatch(): Boolean {
        return 1 / 0 == 1
    }


}