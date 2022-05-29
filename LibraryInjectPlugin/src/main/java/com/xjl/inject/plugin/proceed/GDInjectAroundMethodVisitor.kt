package com.xjl.inject.plugin.proceed

import com.kuaikan.library.libknifeasm.AsmConstant
import com.kuaikan.library.libknifeutil.util.Log
import com.kuaikan.library.libknifeutil.util.StringUtil
import com.xjl.gdinject.annotation.signature.AnnotationSignatureEnum
import com.xjl.inject.plugin.collect.BeCallerMethod
import com.xjl.inject.plugin.collect.GlobalCollectorContainer
import com.xjl.inject.plugin.collect.SourceRecordMethod
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

class GDInjectAroundMethodVisitor(val sourceMethodInfo: SourceMethodInfo, originMethodVisitor: MethodVisitor): AdviceAdapter(AsmConstant.ASM_VERSION, originMethodVisitor, sourceMethodInfo.access, sourceMethodInfo.methodName, sourceMethodInfo.methodDesc) {

    private val sourceRecordMethod: SourceRecordMethod?
            get() {
        if (sourceMethodInfo.className?.contains("MainActivity") == true) {
            Log.e("xjl", "start try catch method")
        }
        val queryTryCatchMethodList =
            GlobalCollectorContainer.getOrCreateByAnnotationSignature(AnnotationSignatureEnum.AnnotationAround.descriptor).injectMap.keys

        return queryTryCatchMethodList.filter {
            it.className == StringUtil.replaceSlash2Dot(
                sourceMethodInfo.className
            )
        }
            .filter { it.methodName == sourceMethodInfo.methodName }
            .firstOrNull { it.methodDesc == sourceMethodInfo.methodDesc }
            .apply {
                this?.isStatic = sourceMethodInfo.access and ACC_STATIC == ACC_STATIC
                if (this?.isStatic == true) {
                    this?.opcode = Opcodes.INVOKESTATIC
                } else {
                    this?.opcode = Opcodes.INVOKEVIRTUAL
                }
            }
    }

    private val beCallMethod: BeCallerMethod
        get() {
        return GlobalCollectorContainer.getOrCreateByAnnotationSignature(AnnotationSignatureEnum.AnnotationAround.descriptor).injectMap[sourceRecordMethod]!!
    }


    override fun onMethodEnter() {
        if (sourceRecordMethod == null || sourceRecordMethod!!.after) {
            return
        }
        Log.e("xjl", "onMethodEnter!!!!!!!")
        ParamCheckUtil.checkParamValid(sourceRecordMethod!!, beCallMethod)
        innerLoadVar()
        visitMethodInsn(Opcodes.INVOKESTATIC, beCallMethod.className, beCallMethod.methodName, beCallMethod.methodDescriptor, false)
    }

    private fun innerLoadVar() {
        val targetParamTypeArray = Type.getArgumentTypes(beCallMethod.methodDescriptor)
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
        if (sourceRecordMethod == null || !sourceRecordMethod!!.after) {
            return
        }
        Log.e("xjl", "onMethodExit!!!!!!!")
        ParamCheckUtil.checkParamValid(sourceRecordMethod!!, beCallMethod)
        innerLoadVar()
        visitMethodInsn(Opcodes.INVOKESTATIC, beCallMethod.className, beCallMethod.methodName, beCallMethod.methodDescriptor, false)
    }
}