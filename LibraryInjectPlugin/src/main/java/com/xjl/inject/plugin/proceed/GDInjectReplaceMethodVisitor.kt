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
        checkParamValid(findSourceRecordMethod, beCallerMethod)
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

    private fun checkParamValid(
        sourceRecordMethod: SourceRecordMethod,
        beCallerMethod: BeCallerMethod
    ) {
        checkParamCount(sourceRecordMethod, beCallerMethod)
        checkParamType(sourceRecordMethod, beCallerMethod)
    }

    private fun checkParamType(
        sourceRecordMethod: SourceRecordMethod,
        beCallerMethod: BeCallerMethod
    ) {
        var beCallerMethodStartCount = 0
        val injectMethodRecordType: Array<Type> =
            Type.getArgumentTypes(sourceRecordMethod.methodDesc)
        val beCallMethodParamTypes: Array<Type> =
            Type.getArgumentTypes(beCallerMethod.methodDescriptor)
        //首先check第一个参数
        if (sourceRecordMethod.opcode == Opcodes.INVOKEVIRTUAL
            || sourceRecordMethod.opcode == Opcodes.INVOKEINTERFACE
            || sourceRecordMethod.opcode == Opcodes.INVOKESPECIAL
        ) {
            val ownerClassName: String = sourceRecordMethod.className ?: ""
            val type: Type = beCallMethodParamTypes[0]
            val firstParamClassName = type.className
            if (!ClassUtil.isSuper(ownerClassName, firstParamClassName)) {
                CloseUtil.exit("$firstParamClassName is a subclass of $ownerClassName or itself! ")
            }
            beCallerMethodStartCount++
        }
        // 接下来按照参数逐个check
        try {
            injectMethodRecordType.forEach {
                val injectSourceClassName = it.className
                val beCallerMethodClassName =
                    beCallMethodParamTypes[beCallerMethodStartCount].className
                if (injectSourceClassName == beCallerMethodClassName) {
                    CloseUtil.exit("param does no the same, injectSourceClassName: $injectSourceClassName, beCallMethodClassName: $beCallerMethodClassName")
                }
                beCallerMethodStartCount++
            }
        } catch (e: Exception) {
            CloseUtil.exit("exception when checkParamType!, e: ${e.stackTrace}")
        }

        if (sourceRecordMethod.needSourceInfo) {
            val needSourceInfoClassName = beCallMethodParamTypes[beCallerMethodStartCount].className
            if (String::class.java.name != needSourceInfoClassName) {
                CloseUtil.exit("source type is error, need string, but now is $needSourceInfoClassName ")
            }
        }
    }

    private fun checkParamCount(
        sourceRecordMethod: SourceRecordMethod,
        beCallerMethod: BeCallerMethod
    ) {
        val injectMethodParamCount: Int = Type.getArgumentTypes(sourceRecordMethod.methodDesc).size
        val beCallMethodParamCount: Int =
            Type.getArgumentTypes(beCallerMethod.methodDescriptor).size
        val needSourceInfo: Boolean = sourceRecordMethod.needSourceInfo
        val targetRealParamCount = beCallMethodParamCount - if (needSourceInfo) 1 else 0
        when (sourceRecordMethod.opcode) {
            Opcodes.INVOKESTATIC -> {
                if (injectMethodParamCount != targetRealParamCount) {
                    CloseUtil.exit("illegal param count, target: $targetRealParamCount, injectCount: $injectMethodParamCount")
                }
            }
            Opcodes.INVOKEVIRTUAL -> {
                if (injectMethodParamCount + 1 != targetRealParamCount) {
                    CloseUtil.exit("illegal param count, target: $targetRealParamCount, injectCount: $injectMethodParamCount")
                }
            }
        }
    }
}