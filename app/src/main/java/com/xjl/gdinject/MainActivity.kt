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
        testIntercept()
    }


    fun testIntercept() {

        innerTestTryAroundParam(2, false)
    }

    fun innerTestTryCatch(): Boolean {
        if (1 / 0 == 1) {
            return false
        }
        return true
    }

    fun innerTestTryCatch1(): Boolean {
        if (1 / 0 == 1) {
            return false
        }
        return true
    }

    fun testResult(): Int {
        if (innerTestTryAroundParam(1, false)) {
            innerTestAround()
            innerTestTryCatch1()
            return 1
        }
        return 2
    }

    fun innerTestTryAroundParam(type: Int, result: Boolean): Boolean {
        if (1 / 0 == 1) {
            return false
        }
        return true
    }

    fun innerTestAround(): Boolean {
        return 1 / 0 == 1
    }


    companion object {
        @JvmStatic
        fun innerTestAroundStatic(type: Int, result: Boolean): Boolean {
            if (1 / 0 == 1) {
                return false
            }
            return true
        }
    }
}