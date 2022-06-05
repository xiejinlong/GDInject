package com.xjl.inject.plugin.proceed

import com.kuaikan.library.libknifeutil.util.Log
import com.xjl.gdinject.annotation.Around
import com.xjl.inject.plugin.collect.BeCallerMethod
import com.xjl.inject.plugin.collect.around.AroundBeHandlerMethod
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

open class GDInjectAroundMethodVisitorVisitor(currentMethodInfo: CurrentMethodInfo, originMethodVisitor: MethodVisitor): BaseAdviceMethodVisitor(currentMethodInfo, originMethodVisitor){


    override fun onMethodEnter() {
        val beCallMethod = getBeCallerMethod(currentMethodInfo)
        val beHandlerMethod = getBeHandlerMethod<AroundBeHandlerMethod>(currentMethodInfo)

        if (beCallMethod == null) {
            return
        }
        if (beHandlerMethod == null || beHandlerMethod.after) {
            return
        }
        Log.e("xjl", "onMethodEnter, need be deal!")
        ParamCheckUtil.checkParamValid(beHandlerMethod, beCallMethod)
        innerLoadVar(beCallMethod)
        visitMethodInsn(Opcodes.INVOKESTATIC, beCallMethod.className, beCallMethod.methodName, beCallMethod.methodDescriptor, false)
    }

    private fun innerLoadVar(beCallerMethod: BeCallerMethod) {
        val targetParamTypeArray = Type.getArgumentTypes(beCallerMethod.methodDescriptor)
        (targetParamTypeArray.indices).forEach {
            when(targetParamTypeArray[it]) {
                Type.BOOLEAN_TYPE, Type.CHAR_TYPE, Type.INT_TYPE, Type.BYTE_TYPE -> mv.visitVarInsn(ILOAD, it)
                Type.FLOAT_TYPE, Type.LONG_TYPE -> mv.visitVarInsn(FLOAD, it)
                Type.DOUBLE_TYPE -> mv.visitVarInsn(DLOAD, it)
                else -> mv.visitVarInsn(ALOAD, it)
            }
        }
    }

    override fun onMethodExit(opcode: Int) {
        val beCallMethod = getBeCallerMethod(currentMethodInfo)
        val beHandlerMethod = getBeHandlerMethod<AroundBeHandlerMethod>(currentMethodInfo)

        if (beCallMethod == null) {
            return
        }
        if (beHandlerMethod == null || !beHandlerMethod.after) {
            return
        }
        Log.e("xjl", "onMethodExit!!!!!!!")
        ParamCheckUtil.checkParamValid(beHandlerMethod, beCallMethod)
        innerLoadVar(beCallMethod)
        visitMethodInsn(Opcodes.INVOKESTATIC, beCallMethod.className, beCallMethod.methodName, beCallMethod.methodDescriptor, false)
    }

    override fun supportAnnotationType(): Class<*> {
        return Around::class.java
    }
}