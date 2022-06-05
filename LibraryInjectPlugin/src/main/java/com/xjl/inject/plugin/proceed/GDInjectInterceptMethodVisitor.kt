package com.xjl.inject.plugin.proceed

import com.kuaikan.library.libknifeutil.util.CloseUtil
import com.kuaikan.library.libknifeutil.util.Log
import com.xjl.gdinject.annotation.Intercept
import com.xjl.inject.plugin.collect.BeCallerMethod
import com.xjl.inject.plugin.collect.intercept.InterceptBeHandlerMethod
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

open class GDInjectInterceptMethodVisitor(
    currentMethodInfo: CurrentMethodInfo,
    originMethodVisitor: MethodVisitor
) : BaseAdviceMethodVisitor(currentMethodInfo, originMethodVisitor) {


    override fun onMethodEnter() {
        val beCallMethod = getBeCallerMethod(currentMethodInfo) ?: return
        val beHandlerMethod = getBeHandlerMethod<InterceptBeHandlerMethod>(currentMethodInfo) ?: return

        try {
            Log.e("xjl", "onMethodExit!!!!!!!- interCept111111")
            ParamCheckUtil.checkParamValid(beHandlerMethod, beCallMethod)
            innerLoadVar(beCallMethod)
            visitMethodInsn(
                Opcodes.INVOKESTATIC,
                beCallMethod.className,
                beCallMethod.methodName,
                beCallMethod.methodDescriptor,
                false
            )
            val l1 = Label()
            visitJumpInsn(IFEQ, l1)
            val l2 = Label()
            visitLabel(l2)
//            visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            visitInsn(RETURN)
            visitLabel(l1)
        } catch (e: Exception) {
            CloseUtil.exit("GDInjectInterceptMethodVisitor message: ${e.message}")
        }
    }

    private fun innerLoadVar(beCallerMethod: BeCallerMethod) {
        val targetParamTypeArray = Type.getArgumentTypes(beCallerMethod.methodDescriptor)
        (targetParamTypeArray.indices).forEach {
            when (targetParamTypeArray[it]) {
                Type.BOOLEAN_TYPE, Type.CHAR_TYPE, Type.INT_TYPE, Type.BYTE_TYPE -> mv.visitVarInsn(
                    ILOAD,
                    it
                )
                Type.FLOAT_TYPE, Type.LONG_TYPE -> mv.visitVarInsn(FLOAD, it)
                Type.DOUBLE_TYPE -> mv.visitVarInsn(DLOAD, it)
                else -> mv.visitVarInsn(ALOAD, it)
            }
        }
    }


    override fun supportAnnotationType(): Class<*> {
        return Intercept::class.java
    }
}