package com.xjl.inject.plugin.proceed

import com.kuaikan.library.libknifeasm.AsmConstant
import com.xjl.gdinject.annotation.Inject
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type

class GDInjectProceedVisitor(classVisitor: ClassVisitor) :
    ClassVisitor(AsmConstant.ASM_VERSION, classVisitor) {

    var currentClassName: String = ""
    private var isInjectClass: Boolean = false

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (descriptor == Type.getDescriptor(Inject::class.java)) {
            isInjectClass = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        name?.let {
            this.currentClassName = it
        }

        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val innerMethodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (isInjectClass) {
            return innerMethodVisitor
        }
        return GDInjectReplaceMethodVisitor(SourceInfo().apply {
            this.className = currentClassName
            this.methodName = name
            this.methodDesc = descriptor
        }, innerMethodVisitor)
    }
}