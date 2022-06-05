package com.xjl.inject.plugin.collect

import com.kuaikan.library.libknifeasm.AsmConstant
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor

class GDInjectCollectMethodVisitor(
    var beCallerMethod: BeCallerMethod,
    methodVisitor: MethodVisitor
) : MethodVisitor(AsmConstant.ASM_VERSION, methodVisitor) {

    private val chain = CollectAnnotationVisitorChain()

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        descriptor ?: return super.visitAnnotation(descriptor, visible)
        val visitor = chain.proceedToVisitor(descriptor)
        visitor?.annotationRecord = GlobalCollectorContainer.getOrCreateByAnnotationSignature(visitor?.supportAnnotation())
        visitor?.beCallerMethod = beCallerMethod
        return visitor?: return super.visitAnnotation(descriptor, visible)
    }
}