package com.xjl.gdinject.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class Replace(val targets: Array<String>,
                         val forceVerify: Boolean = false,
                         val needSourceInfo: Boolean = false)