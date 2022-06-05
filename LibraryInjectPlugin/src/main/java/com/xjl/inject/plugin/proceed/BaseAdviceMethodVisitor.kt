package com.xjl.inject.plugin.proceed

import com.kuaikan.library.libknifeasm.AsmConstant
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

abstract class BaseAdviceMethodVisitor(val currentMethodInfo: CurrentMethodInfo, originMethodVisitor: MethodVisitor): AdviceAdapter(AsmConstant.ASM_VERSION, originMethodVisitor, currentMethodInfo.methodAccess, currentMethodInfo.methodName, currentMethodInfo.methodDesc), IInjectMethodVisitor {

}