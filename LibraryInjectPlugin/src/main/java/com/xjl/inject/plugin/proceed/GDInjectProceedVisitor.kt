package com.xjl.inject.plugin.proceed

import com.kuaikan.library.libknifeasm.AsmConstant
import org.objectweb.asm.ClassVisitor

class GDInjectProceedVisitor(classVisitor: ClassVisitor): ClassVisitor(AsmConstant.ASM_VERSION, classVisitor) {

}