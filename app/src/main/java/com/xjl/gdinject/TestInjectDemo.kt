package com.xjl.gdinject

import com.xjl.gdinject.annotation.Inject
import com.xjl.gdinject.annotation.Replace

@Inject
class TestInjectDemo {
    companion object {

        @Replace(targets = ["com.xjl.gdinject.TestThread.test()V"], needSourceInfo = true)
        @JvmStatic
        fun hookTestThread(thread: TestThread, sourceInfo: String) {
            thread.test()
        }


        @Replace(targets = ["com.xjl.gdinject.TestThread.test1()V"])
        @JvmStatic
        fun hookTestThread1(thread: TestThread) {
            thread.test()
        }

    }
}