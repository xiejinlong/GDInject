package com.xjl.gdinject.annotation

import kotlin.reflect.KClass

/**
 * todo:// change method config，opt need
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class TargetClass(val clazz: KClass<*>)

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class TargetClassName(val clazzName: String)

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class TargetMethodName(val methodName: String)

