package com.xjl.inject.plugin.proceed

import com.kuaikan.library.libknifeasm.AsmConstant
import com.kuaikan.library.libknifeutil.util.ClassUtil
import com.kuaikan.library.libknifeutil.util.CloseUtil
import com.kuaikan.library.libknifeutil.util.Log
import com.kuaikan.library.libknifeutil.util.StringUtil
import com.xjl.gdinject.annotation.signature.AnnotationSignatureEnum
import com.xjl.inject.plugin.collect.BeCallerMethod
import com.xjl.inject.plugin.collect.GlobalCollectorContainer
import com.xjl.inject.plugin.collect.SourceRecordMethod
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

class GDInjectReplaceMethodVisitor(
    private val sourceMethodInfo: SourceMethodInfo,
    originMethodVisitor: MethodVisitor
) :
    MethodVisitor(AsmConstant.ASM_VERSION, originMethodVisitor) {

    override fun visitMethodInsn(
        opcode: Int,
        owner: String,
        name: String,
        descriptor: String?,
        isInterface: Boolean
    ) {
//        if (owner?.contains("MainActivity") == true || owner?.contains("TestThread") == true) {
//            System.out.println("1")
//        }
        val queryCurrentReplaceMethodList =
            GlobalCollectorContainer.getOrCreateByAnnotationSignature(AnnotationSignatureEnum.AnnotationReplace.descriptor).injectMap.keys
        if (name.contains("test")) {
            Log.e("xjl", "it.className: ${StringUtil.replaceSlash2Dot(owner)}, methodName: ${name}, methodDesc: ${descriptor}")
        }
        val findSourceRecordMethod: SourceRecordMethod? =
            queryCurrentReplaceMethodList.filter { it.className == StringUtil.replaceSlash2Dot(owner) }
                .filter { it.methodName == name }.firstOrNull { it.methodDesc == descriptor }

        if (findSourceRecordMethod == null) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
            return
        }
        val beCallerMethod =
            GlobalCollectorContainer.getOrCreateByAnnotationSignature(AnnotationSignatureEnum.AnnotationReplace.descriptor)
                .injectMap[findSourceRecordMethod]
        if (beCallerMethod == null) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
            return
        }
        Log.e("xjl", "try replace....${beCallerMethod.className}:${beCallerMethod.methodName}:${beCallerMethod.methodDescriptor}")
        findSourceRecordMethod.opcode = opcode
        ParamCheckUtil.checkParamValid(findSourceRecordMethod, beCallerMethod)
        startReplaceMethod(findSourceRecordMethod, beCallerMethod)
    }

    private fun startReplaceMethod(
        sourceRecordMethod: SourceRecordMethod,
        beCallerMethod: BeCallerMethod
    ) {
        if (sourceRecordMethod.needSourceInfo) {
            val sourceInfo: String =
                sourceMethodInfo.className + " : " + sourceMethodInfo.methodName + " : " + sourceMethodInfo.methodDesc
            visitLdcInsn(sourceInfo)
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