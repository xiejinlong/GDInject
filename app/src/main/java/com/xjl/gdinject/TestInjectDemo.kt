package com.xjl.gdinject

import com.xjl.gdinject.annotation.Inject
import com.xjl.gdinject.annotation.Replace
import com.xjl.gdinject.annotation.TryCatch

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

        @TryCatch(targets = ["com.xjl.gdinject.TestThread.test1()V"])
        @JvmStatic
        fun catchTestThread1(throwable: Throwable) {
            println(throwable.message)
        }

        @TryCatch(targets = ["com.xjl.gdinject.TestThread.test2()I"])
        @JvmStatic
        fun catchTestThread2(throwable: Throwable): Int {
            println(throwable.message)
            return -1
        }


    }
}