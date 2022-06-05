package com.xjl.gdinject.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class Intercept(
    val targets: Array<String>,
    val extend: Boolean = false,
    val callerList: Array<String> = [],
    val blackList: Array<String> = [],
    val supered: Boolean = false
)
