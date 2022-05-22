package com.xjl.gdinject.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class TryCatch(val targets: Array<String>,
                          val extend: Boolean = false,
                          val supered: Boolean = false)