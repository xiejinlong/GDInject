package com.xjl.inject.plugin.proceed

import com.android.tools.build.jetifier.core.utils.Log
import com.kuaikan.library.libknifeutil.util.ClassUtil
import com.xjl.gdinject.annotation.Replace
import com.xjl.inject.plugin.collect.BeCallerMethod
import com.xjl.inject.plugin.collect.BeHandlerMethod
import com.xjl.inject.plugin.collect.replace.ReplaceBeHandlerMethod
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class GDInjectReplaceMethodVisitor(
    currentMethodInfo: CurrentMethodInfo,
    private val originMethodVisitor: MethodVisitor
) :
    BaseMethodVisitor(currentMethodInfo, originMethodVisitor) {

    /**
     * for replace <init>
     * should ignore NEW 、 DUP
     */
    private val pendingAction: MutableList<Runnable> = mutableListOf()

//    override fun visitTypeInsn(opcode: Int, type: String?) {
//        if (opcode != Opcodes.NEW) {
//            super.visitTypeInsn(opcode, type)
//            return
//        }
//        if (type == null) {
//            super.visitTypeInsn(opcode, type)
//            return
//        }
//        val beHandlerMethod: BeHandlerMethod? = getBeHandlerConstruct(type)
//        if (beHandlerMethod == null) {
//            super.visitTypeInsn(opcode, type)
//            return
//        }
//        Log.e("XJL", "collect new ....., type is: $type")
//        pendingAction.add(Runnable {
//            super.visitTypeInsn(opcode, type)
//        })
//    }
//
//    override fun visitInsn(opcode: Int) {
//        if (opcode != Opcodes.DUP) {
//            super.visitInsn(opcode)
//            return
//        }
//        if (pendingAction.isEmpty()) {
//            return
//        }
//        pendingAction.add(Runnable {
//            super.visitInsn(opcode)
//        })
//    }

    @Synchronized
    fun executePendingAction(type: String) {
//        if (pendingAction.isEmpty()) {
//            return
//        }
//        Log.e("XJL", "execute.. new ..... $type")
//        pendingAction.forEach {
//
//            it.run()
//        }
//        pendingAction.clear()
    }

    override fun visitMethodInsn(
        opcode: Int,
        owner: String,
        name: String,
        descriptor: String?,
        isInterface: Boolean
    ) {
        val refactorCurrentMethodInfo = CurrentMethodInfo().apply {
            this.methodAccess = opcode
            this.className = owner
            this.methodName = name
            this.methodDesc = descriptor
        }
        val beCallMethod = getBeCallerMethod(refactorCurrentMethodInfo)
        if (beCallMethod == null) {
            executePendingAction(owner)
            return super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        }

        val beHandlerMethod = getBeHandlerMethod<ReplaceBeHandlerMethod>(refactorCurrentMethodInfo)
        if (beHandlerMethod == null) {
            executePendingAction(owner)
            return super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        }
        if (name != "<init>") {
            executePendingAction(owner)
        }
        ParamCheckUtil.checkParamValid(beHandlerMethod, beCallMethod)
        if (isSameMethodReplace(refactorCurrentMethodInfo, beCallMethod)) {
            return
        }
        startReplaceMethod(beHandlerMethod, beCallMethod)
        if (name == "<init>" && pendingAction.isNotEmpty()) {
//            visitVarInsn()
        }
    }

    override fun supportAnnotationType(): Class<*> {
        return Replace::class.java
    }

    /**
     * 解决同一个方法下， 在Replace替换下造成递归导致的 StackOverflowException
     * @return
     */
    private fun isSameMethodReplace(
        currentMethodInfo: CurrentMethodInfo,
        beCallerMethod: BeCallerMethod
    ): Boolean {
        return if (currentMethodInfo.methodName != beCallerMethod.methodName) {
            false
        } else ClassUtil.isSuper(currentMethodInfo.className, beCallerMethod.className) &&
                currentMethodInfo.className == beCallerMethod.className &&
                currentMethodInfo.methodDesc == beCallerMethod.methodDescriptor
    }

    private fun startReplaceMethod(
        beHandlerMethod: ReplaceBeHandlerMethod,
        beCallerMethod: BeCallerMethod
    ) {
        if (beHandlerMethod.needSourceInfo) {
            val sourceInfo: String =
                currentMethodInfo.className + " : " + currentMethodInfo.methodName + " : " + currentMethodInfo.methodDesc
            visitLdcInsn(sourceInfo)
        }
        originMethodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            beCallerMethod.className!!,
            beCallerMethod.methodName!!,
            beCallerMethod.methodDescriptor,
            false
        )
    }


}