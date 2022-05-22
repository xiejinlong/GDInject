package com.xjl.gdinject.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class Around(val targets: Array<String>,
                          val before: Boolean = false)