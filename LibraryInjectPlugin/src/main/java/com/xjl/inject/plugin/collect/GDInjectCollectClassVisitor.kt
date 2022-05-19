package com.xjl.inject.plugin.collect

import com.kuaikan.library.libknifeasm.AsmConstant
import com.xjl.gdinject.annotation.signature.AnnotationSignatureEnum
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class GDInjectCollectClassVisitor(classVisitor: ClassVisitor): ClassVisitor(AsmConstant.ASM_VERSION, classVisitor) {

    var injectClass = false
    var collectorClassName: String? = null


    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        collectorClassName = name
        return super.visit(version, access, name, signature, superName, interfaces)
    }
    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (descriptor == AnnotationSignatureEnum.AnnotationInject.descriptor) {
            injectClass = true
        }
       return super.visitAnnotation(descriptor, visible)
    }
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        if (!injectClass) {
            return super.visitMethod(access, name, descriptor, signature, exceptions)
        }
        val beCallerMethod = BeCallerMethod().apply {
            this.access = access
            this.methodName = name
            this.methodDescriptor = descriptor
            this.methodSignature = signature
            this.methodExceptions = exceptions
        }
        return GDInjectCollectMethodVisitor(beCallerMethod, super.visitMethod(access, name, descriptor, signature, exceptions))
    }
}