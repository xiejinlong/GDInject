package com.xjl.inject.plugin.proceed

import com.kuaikan.library.libknifeutil.util.Log
import com.kuaikan.library.libknifeutil.util.StringUtil
import com.xjl.inject.plugin.collect.BeCallerMethod
import com.xjl.inject.plugin.collect.BeHandlerMethod
import com.xjl.inject.plugin.collect.GlobalCollectorContainer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

interface IInjectMethodVisitor {

    fun getBeHandlerConstruct(className: String): BeHandlerMethod? {

        val currentHandlerMethodList =
            GlobalCollectorContainer.getOrCreateByAnnotationSignature(supportAnnotationType()).injectMap.keys

        val beHandlerMethod = currentHandlerMethodList.filter {
            it.className == StringUtil.replaceSlash2Dot(
                className
            )
        }
            .firstOrNull { it.methodName == "<init>" }
        if (beHandlerMethod == null) {
            return beHandlerMethod
        }
        if (!BlackListUtil.canDealThisMethod(
                className,
                beHandlerMethod as BeHandlerMethod
            )
        ) {
            Log.e("XJL", "ignore this method by black config.....")
            return null
        }
        return beHandlerMethod
    }

    fun <T> getBeHandlerMethod(currentMethodInfo: CurrentMethodInfo): T? {
        if (currentMethodInfo.className?.contains("MainActivity") == true) {
            Log.e("XJL", "11111")
        }

        val currentHandlerMethodList =
            GlobalCollectorContainer.getOrCreateByAnnotationSignature(supportAnnotationType()).injectMap.keys

        val beHandlerMethod = currentHandlerMethodList.filter {
            it.className == StringUtil.replaceSlash2Dot(
                currentMethodInfo.className
            )
        }
            .filter { it.methodName == currentMethodInfo.methodName }
            .firstOrNull { it.methodDesc == currentMethodInfo.methodDesc }
            .apply {
                this?.isStatic =
                    currentMethodInfo.methodAccess and AdviceAdapter.ACC_STATIC == AdviceAdapter.ACC_STATIC
                if (this?.isStatic == true) {
                    this?.opcode = Opcodes.INVOKESTATIC
                } else {
                    this?.opcode = Opcodes.INVOKEVIRTUAL
                }
            } as? T
        if (beHandlerMethod == null) {
            return beHandlerMethod
        }
        if (!BlackListUtil.canDealThisMethod(
                currentMethodInfo.className!!,
                beHandlerMethod as BeHandlerMethod
            )
        ) {
            Log.e("XJL", "ignore this method by black config.....")
            return null
        }
        return beHandlerMethod
    }


    fun getBeCallerMethod(currentMethodInfo: CurrentMethodInfo): BeCallerMethod? {
        val beHandlerMethod = getBeHandlerMethod<BeHandlerMethod>(currentMethodInfo) ?: return null
        return GlobalCollectorContainer.getOrCreateByAnnotationSignature(supportAnnotationType()).injectMap[beHandlerMethod]!!
    }

    /**
     * 表示需要处理的Annotation
     */
    fun supportAnnotationType(): Class<*>
}