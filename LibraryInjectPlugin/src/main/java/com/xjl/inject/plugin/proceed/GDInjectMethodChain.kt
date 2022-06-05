package com.xjl.inject.plugin.proceed

import org.objectweb.asm.MethodVisitor

object GDInjectMethodChain {

    fun proceedChain(
        currentMethodInfo: CurrentMethodInfo,
        innerMethodVisitor: MethodVisitor
    ): MethodVisitor {

        val interceptMethodVisitor =
            GDInjectInterceptMethodVisitor(currentMethodInfo, innerMethodVisitor)
        val gdInjectAroundVisitor =
            GDInjectAroundMethodVisitorVisitor(currentMethodInfo, interceptMethodVisitor)
        val tryCatchMethodVisitor = GDInjectTryCatchMethodVisitor(currentMethodInfo, gdInjectAroundVisitor)
        val replaceMethodVisitor =
            GDInjectReplaceMethodVisitor(currentMethodInfo, tryCatchMethodVisitor)
        return replaceMethodVisitor
    }
}