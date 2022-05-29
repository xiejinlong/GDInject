package com.xjl.gdinject

import com.xjl.gdinject.annotation.Around
import com.xjl.gdinject.annotation.Inject
import com.xjl.gdinject.annotation.Replace
import com.xjl.gdinject.annotation.TryCatch

@Inject
class TestInjectDemo {
    companion object {

        @Replace(targets = ["com.xjl.gdinject.MainRealDemoThread.test()V"], needSourceInfo = true)
        @JvmStatic
        fun hookTestThread(thread: MainRealDemoThread, sourceInfo: String) {
            thread.test()
        }

        @JvmStatic
        @Around(targets = ["com.xjl.gdinject.MainActivity.innerTestAround()Z"])
        fun aroundTestThread11111(activity: MainActivity): Boolean {
            System.out.println("nothing")
            return false
        }

        @JvmStatic
        @Around(targets = ["com.xjl.gdinject.MainActivity.innerTestTryCatch1()Z"],after = true)
        fun aroundTestThread22222(mainActivity: MainActivity) {
            System.out.println("nothing")
        }


        @JvmStatic
        @Around(targets = ["com.xjl.gdinject.MainActivity.innerTestTryAroundParam(IZ)Z"],after = true)
        fun aroundTestThread3333(activity: MainActivity, type: Int, result: Boolean) {
            System.out.println("nothing, activity: ${activity}, $type, $result")
        }

        @JvmStatic
        @Around(targets = ["com.xjl.gdinject.MainActivity.innerTestAroundStatic(IZ)Z"])
        fun aroundTestThread4444(type: Int, result: Boolean) {
            System.out.println("nothing, activity: , $type, $result")
        }

        @Replace(targets = ["com.xjl.gdinject.MainRealDemoThread.test1()V"])
        @JvmStatic
        fun hookTestThread1(thread: MainRealDemoThread) {
            thread.test()
        }

        @TryCatch(targets = ["com.xjl.gdinject.MainRealDemoThread.test1()V"])
        @JvmStatic
        fun catchTestThread1(throwable: Throwable) {
            println(throwable.message)
        }

        @TryCatch(targets = ["com.xjl.gdinject.MainActivity.onCreate(Landroid/os/Bundle;)V"])
        @JvmStatic
        fun catchMainOnCreate(throwable: Throwable) {
            println(throwable.message)
        }

        @TryCatch(targets = ["com.xjl.gdinject.MainActivity.innerTestTryCatch()Z"])
        @JvmStatic
        fun catchMainMethod(throwable: Throwable): Boolean {
            println(throwable.message)
            return true
        }

        @TryCatch(targets = ["com.xjl.gdinject.MainRealDemoThread.test2()I"])
        @JvmStatic
        fun catchTestThread2(throwable: Throwable): Int {
            println(throwable.message)
            return -1
        }


    }
}