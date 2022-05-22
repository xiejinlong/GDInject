package com.xjl.inject.plugin.proceed

import com.kuaikan.library.libknifeasm.AsmConstant
import com.kuaikan.library.libknifeutil.util.CloseUtil
import com.kuaikan.library.libknifeutil.util.Log
import com.kuaikan.library.libknifeutil.util.StringUtil
import com.xjl.gdinject.annotation.signature.AnnotationSignatureEnum
import com.xjl.inject.plugin.collect.BeCallerMethod
import com.xjl.inject.plugin.collect.GlobalCollectorContainer
import com.xjl.inject.plugin.collect.SourceRecordMethod
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

class GDInjectTryCatchMethodVisitor(
    var sourceMethodInfo: SourceMethodInfo,
    var sourceMethodVisitor: MethodVisitor
) : MethodVisitor(AsmConstant.ASM_VERSION, sourceMethodVisitor) {


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

    private val sourceRecordMethod: SourceRecordMethod? by lazy {
        if (sourceMethodInfo.className?.contains("Test") == true) {
            Log.e("xjl", "start try catch method")
        }
        val queryTryCatchMethodList =
            GlobalCollectorContainer.getOrCreateByAnnotationSignature(AnnotationSignatureEnum.AnnotationTryCatch.descriptor).injectMap.keys

        queryTryCatchMethodList.filter {
            it.className == StringUtil.replaceSlash2Dot(
                sourceMethodInfo.className
            )
        }
            .filter { it.methodName == sourceMethodInfo.methodName }
            .firstOrNull { it.methodDesc == sourceMethodInfo.methodDesc }
    }

    private val beCallMethod: BeCallerMethod by lazy {
        GlobalCollectorContainer.getOrCreateByAnnotationSignature(AnnotationSignatureEnum.AnnotationTryCatch.descriptor).injectMap[sourceRecordMethod]!!
    }

    /**
     * 首次进入方法体调用
     */
    override fun visitCode() {
        super.visitCode()
        if (sourceRecordMethod == null) {
            return
        }
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
        if (sourceRecordMethod == null) {
            super.visitMaxs(maxStack, maxLocals)
            return
        }
        visitLabel(endLabel)
        visitJumpInsn(Opcodes.GOTO, returnLabel)
        visitLabel(handleLabel)

        visitVarInsn(Opcodes.ASTORE, 1)
        visitVarInsn(Opcodes.ALOAD, 1)
        innerCallDestMethod()
        visitLabel(returnLabel)
        visitReturnOpcodes()

        Log.e(
            "xjl",
            "inner try catch....${beCallMethod.className}:${beCallMethod.methodName}:${beCallMethod.methodDescriptor}"
        )
        super.visitMaxs(maxStack + 2, maxLocals + 2)
    }

    private fun visitReturnOpcodes() {
        val methodDesc: String = beCallMethod.methodDescriptor ?: ""
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

    private fun innerCallDestMethod() {
        val targetParamTypes: Array<Type> = Type.getArgumentTypes(beCallMethod?.methodDescriptor)
        if (targetParamTypes.size != 1) {
            CloseUtil.exit("tryCatch just enable 1 param")
        }
        if (targetParamTypes[0] != Type.getType(Throwable::class.java)) {
            CloseUtil.exit("tryCatch just enable param type throwable")
        }
        super.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            beCallMethod.className,
            beCallMethod.methodName,
            beCallMethod.methodDescriptor,
            false
        )
    }
}