package com.xjl.inject.plugin.proceed

import com.kuaikan.library.libknifeasm.AsmConstant
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class GDInjectProceedVisitor(classVisitor: ClassVisitor) :
    ClassVisitor(AsmConstant.ASM_VERSION, classVisitor) {

    var currentClassName: String = ""

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
        return GDInjectReplaceMethodVisitor(SourceInfo().apply {
            this.className = currentClassName
            this.methodName = name
            this.methodDesc = descriptor
        }, innerMethodVisitor)
    }
}