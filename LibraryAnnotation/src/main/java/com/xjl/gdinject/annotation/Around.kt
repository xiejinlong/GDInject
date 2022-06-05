package com.xjl.gdinject.annotation


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class Around(
    val targets: Array<String>,
    val after: Boolean = false,
    val callerList: Array<String> = [],
    val blackList: Array<String> = [],
    val needSourceInfo: Boolean = false
)