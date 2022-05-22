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
import org.objectweb.asm.commons.AdviceAdapter

class GDInjectAroundMethodVisitor(val sourceMethodInfo: SourceMethodInfo, originMethodVisitor: MethodVisitor): AdviceAdapter(AsmConstant.ASM_VERSION, originMethodVisitor, sourceMethodInfo.access, sourceMethodInfo.methodName, sourceMethodInfo.methodDesc) {

    private val sourceRecordMethod: SourceRecordMethod? by lazy {
        if (sourceMethodInfo.className?.contains("Test") == true) {
            Log.e("xjl", "start try catch method")
        }
        val queryTryCatchMethodList =
            GlobalCollectorContainer.getOrCreateByAnnotationSignature(AnnotationSignatureEnum.AnnotationAround.descriptor).injectMap.keys

        queryTryCatchMethodList.filter {
            it.className == StringUtil.replaceSlash2Dot(
                sourceMethodInfo.className
            )
        }
            .filter { it.methodName == sourceMethodInfo.methodName }
            .firstOrNull { it.methodDesc == sourceMethodInfo.methodDesc }
    }

    private val beCallMethod: BeCallerMethod by lazy {
        GlobalCollectorContainer.getOrCreateByAnnotationSignature(AnnotationSignatureEnum.AnnotationAround.descriptor).injectMap[sourceRecordMethod]!!
    }


    override fun onMethodEnter() {
//        val isStatic = (Opcodes.ACC_STATIC and sourceMethodInfo.access) == Opcodes.ACC_STATIC
//        checkParamType() {}
//        visitMethodInsn(Opcodes.INVOKESTATIC, beCallMethod.className, beCallMethod.methodName, beCallMethod.methodDescriptor, false)
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
    }
}