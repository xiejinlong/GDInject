package com.xjl.inject.plugin.proceed

import com.kuaikan.library.libknifeasm.AsmConstant
import org.objectweb.asm.MethodVisitor

abstract class BaseMethodVisitor(
    val currentMethodInfo: CurrentMethodInfo,
    originMethodVisitor: MethodVisitor
) : MethodVisitor(AsmConstant.ASM_VERSION, originMethodVisitor), IInjectMethodVisitor {


}