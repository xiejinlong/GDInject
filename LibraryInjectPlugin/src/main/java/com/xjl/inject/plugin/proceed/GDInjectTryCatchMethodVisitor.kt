package com.xjl.inject.plugin.proceed

import com.kuaikan.library.libknifeutil.util.CloseUtil
import com.kuaikan.library.libknifeutil.util.Log
import com.xjl.gdinject.annotation.TryCatch
import com.xjl.inject.plugin.collect.BeCallerMethod
import com.xjl.inject.plugin.collect.BeHandlerMethod
import com.xjl.inject.plugin.collect.trycatch.TryCatchBeHandlerMethod
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

class GDInjectTryCatchMethodVisitor(
    currentMethodInfo: CurrentMethodInfo,
    sourceMethodVisitor: MethodVisitor
) : BaseMethodVisitor(currentMethodInfo, sourceMethodVisitor) {


    companion object {
        const val TYPE_THROWABLE = "java/lang/Throwable"
    }

    /**
     * try catch存在start、endLabel
     */

    private val startLabel = Label()
    private val endLabel = Label()

    /**
     * cache的处理Label
     */
    private val handleLabel = Label()

    /**
     * return语句label
     */
    private val returnLabel = Label()

    val beCallerMethod: BeCallerMethod? by lazy {
        getBeCallerMethod(currentMethodInfo)
    }

    val beHandlerMethod: BeHandlerMethod? by lazy {
        getBeHandlerMethod<TryCatchBeHandlerMethod>(currentMethodInfo)
    }

    /**
     * 首次进入方法体调用
     */
    override fun visitCode() {
        super.visitCode()
        val beCallMethod =  beCallerMethod?: return
        Log.e(
            "xjl",
            "try try catch....${beCallMethod.className}:${beCallMethod.methodName}:${beCallMethod.methodDescriptor}"
        )
        super.visitTryCatchBlock(startLabel, endLabel, handleLabel, TYPE_THROWABLE)
        visitLabel(startLabel)
    }

    /**
     * 方法结束时调用，需要重新计算局部便量表和操作数栈的大小
     */
    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        val beCallMethod = beCallerMethod ?: return super.visitMaxs(maxStack, maxLocals)
        visitLabel(endLabel)
        visitJumpInsn(Opcodes.GOTO, returnLabel)
        visitLabel(handleLabel)

        visitVarInsn(Opcodes.ASTORE, 1)
        visitVarInsn(Opcodes.ALOAD, 1)
        innerCallDestMethod(beCallMethod)
        visitLabel(returnLabel)
        visitReturnOpcodes(beCallMethod)

        Log.e(
            "xjl",
            "inner try catch....${beCallMethod.className}:${beCallMethod.methodName}:${beCallMethod.methodDescriptor}"
        )
        super.visitMaxs(maxStack + 2, maxLocals + 2)
    }

    override fun supportAnnotationType(): Class<*> {
        return TryCatch::class.java
    }

    private fun visitReturnOpcodes(beCallerMethod: BeCallerMethod) {
        val methodDesc: String = beCallerMethod.methodDescriptor ?: ""
        if (methodDesc.contains(")V")) {
            visitInsn(Opcodes.RETURN)
        } else if (methodDesc.contains(")I") || methodDesc.contains(")Z") || methodDesc.contains(")B") || methodDesc.contains(")C")) {
            visitInsn(Opcodes.IRETURN)
        } else if (methodDesc.contains(")J")) {
            visitInsn(Opcodes.LRETURN)
        } else if ((methodDesc.contains(")L")
                    && methodDesc.substring(methodDesc.length - 1) == ";")
            || methodDesc.contains(")[")
        ) {
            visitInsn(Opcodes.ARETURN)
        } else if (methodDesc.contains(")D")) {
            visitInsn(Opcodes.DRETURN)
        } else if (methodDesc.contains(")F")) {
            visitInsn(Opcodes.FRETURN)
        }
        else {
            visitInsn(Opcodes.RETURN)
        }
    }

    private fun innerCallDestMethod(beCallerMethod: BeCallerMethod) {
        val targetParamTypes: Array<Type> = Type.getArgumentTypes(beCallerMethod.methodDescriptor)
        if (targetParamTypes.size != 1) {
            CloseUtil.exit("tryCatch just enable 1 param")
        }
        if (targetParamTypes[0] != Type.getType(Throwable::class.java)) {
            CloseUtil.exit("tryCatch just enable param type throwable")
        }
        super.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            beCallerMethod.className,
            beCallerMethod.methodName,
            beCallerMethod.methodDescriptor,
            false
        )
    }
}