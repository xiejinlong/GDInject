package com.xjl.inject.plugin.proceed

import com.kuaikan.library.libknifeasm.AsmConstant
import com.kuaikan.library.libknifeutil.util.ClassUtil
import com.kuaikan.library.libknifeutil.util.CloseUtil
import com.kuaikan.library.libknifeutil.util.StringUtil
import com.xjl.gdinject.annotation.signature.AnnotationSignatureEnum
import com.xjl.inject.plugin.collect.BeCallerMethod
import com.xjl.inject.plugin.collect.GlobalCollectorContainer
import com.xjl.inject.plugin.collect.InjectMethodRecord
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

class GDInjectReplaceMethodVisitor(
    private val sourceInfo: SourceInfo,
    originMethodVisitor: MethodVisitor
) :
    MethodVisitor(AsmConstant.ASM_VERSION, originMethodVisitor) {

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        val queryCurrentReplaceMethodList =
            GlobalCollectorContainer.getOrCreateByAnnotationSignature(AnnotationSignatureEnum.AnnotationReplace.descriptor).injectMap.keys
        val findInjectMethodRecord: InjectMethodRecord? =
            queryCurrentReplaceMethodList.filter { it.className == StringUtil.replaceSlash2Dot(owner) }
                .filter { it.methodName == name }.firstOrNull { it.methodDesc == descriptor }
        if (findInjectMethodRecord == null) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
            return
        }
        val beCallerMethod =
            GlobalCollectorContainer.getOrCreateByAnnotationSignature(AnnotationSignatureEnum.AnnotationReplace.descriptor)
                .injectMap[findInjectMethodRecord]
        if (beCallerMethod == null) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
            return
        }
        findInjectMethodRecord.opcode = opcode
        checkParamValid(findInjectMethodRecord, beCallerMethod)
        startReplaceMethod(findInjectMethodRecord, beCallerMethod)
    }

    private fun startReplaceMethod(
        injectMethodRecord: InjectMethodRecord,
        beCallerMethod: BeCallerMethod
    ) {
        if (injectMethodRecord.needSourceInfo) {
            val sourceInfo: String =
                sourceInfo.className + " : " + sourceInfo.methodName + " : " + sourceInfo.methodDesc
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
        injectMethodRecord: InjectMethodRecord,
        beCallerMethod: BeCallerMethod
    ) {
        checkParamCount(injectMethodRecord, beCallerMethod)
        checkParamType(injectMethodRecord, beCallerMethod)
    }

    private fun checkParamType(
        injectMethodRecord: InjectMethodRecord,
        beCallerMethod: BeCallerMethod
    ) {
        var beCallerMethodStartCount = 0
        val injectMethodRecordType: Array<Type> =
            Type.getArgumentTypes(injectMethodRecord.methodDesc)
        val beCallMethodParamTypes: Array<Type> =
            Type.getArgumentTypes(beCallerMethod.methodDescriptor)
        //首先check第一个参数
        if (injectMethodRecord.opcode == Opcodes.INVOKEVIRTUAL
            || injectMethodRecord.opcode == Opcodes.INVOKEINTERFACE
            || injectMethodRecord.opcode == Opcodes.INVOKESPECIAL
        ) {
            val ownerClassName: String = injectMethodRecord.className ?: ""
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

        if (injectMethodRecord.needSourceInfo) {
            val needSourceInfoClassName = beCallMethodParamTypes[beCallerMethodStartCount].className
            if (String::class.java.name != needSourceInfoClassName) {
                CloseUtil.exit("source type is error, need string, but now is $needSourceInfoClassName ")
            }
        }
    }

    private fun checkParamCount(
        injectMethodRecord: InjectMethodRecord,
        beCallerMethod: BeCallerMethod
    ) {
        val injectMethodParamCount: Int = Type.getArgumentTypes(injectMethodRecord.methodDesc).size
        val beCallMethodParamCount: Int =
            Type.getArgumentTypes(beCallerMethod.methodDescriptor).size
        val needSourceInfo: Boolean = injectMethodRecord.needSourceInfo
        val targetRealParamCount = beCallMethodParamCount - if (needSourceInfo) 1 else 0
        when (injectMethodRecord.opcode) {
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