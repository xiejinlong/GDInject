package com.xjl.inject.plugin.collect

import com.kuaikan.library.libknifeasm.AsmConstant
import com.kuaikan.library.libknifeutil.util.ClassUtil
import com.kuaikan.library.libknifeutil.util.CloseUtil
import com.kuaikan.library.libknifeutil.util.Log
import com.kuaikan.library.libknifeutil.util.RegexUtil
import com.xjl.gdinject.annotation.signature.AnnotationParam
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor

class GDInjectCollectMethodVisitor(
    var beCallerMethod: BeCallerMethod,
    methodVisitor: MethodVisitor
) : MethodVisitor(AsmConstant.ASM_VERSION, methodVisitor) {

    var extend: Boolean = false
    var supered: Boolean = false
    var forceVerify: Boolean = true
    var needSourceInfo: Boolean = false

    var targets: ArrayList<String> = arrayListOf()
    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (!CollectorAnnotationUtil.shouldCollectorSource(descriptor)) {
            return super.visitAnnotation(descriptor, visible)
        }
        return GDInjectAnnotationVisitor(
            GlobalCollectorContainer.getOrCreateByAnnotationSignature(
                descriptor
            )
        )
    }

    inner class GDInjectAnnotationVisitor(var annotationRecord: InjectAnnotationRecord) :
        AnnotationVisitor(AsmConstant.ASM_VERSION) {
        override fun visit(name: String?, value: Any?) {
            when (name) {
                AnnotationParam.PARAM_EXTEND -> extend = (value as? Boolean) ?: false
                AnnotationParam.PARAM_SUPERED -> supered = (value as? Boolean) ?: false
                AnnotationParam.PARAM_VERIFY -> forceVerify = (value as? Boolean) ?: false
                AnnotationParam.PARAM_NEED_SOURCE_INFO -> needSourceInfo =
                    (value as? Boolean) ?: false
            }
        }

        override fun visitArray(name: String?): AnnotationVisitor {
            if (name != AnnotationParam.PARAM_TARGETS) {
                return super.visitArray(name)
            }
            return GDInjectAnnotationArrayVisitor()
        }

        /**
         * parse param end.
         */
        override fun visitEnd() {
            super.visitEnd()
            Log.e("xjl", "visit end... target size: ${targets.size}")
            targets.forEach { target ->
                Log.e("xjl", "collect target is: $target")
                val className = target.substring(0, target.lastIndexOf("."))
                val methodName = target.substring(target.lastIndexOf(".") + 1, target.indexOf("("))
                val methodDesc = target.substring(target.indexOf("("), target.length)
                if (!ClassUtil.isMethodExist(target, className, methodName, methodDesc)) {
                    if (forceVerify) {
                        CloseUtil.exit("$target is not exists !")
                    }
                }
                annotationRecord.injectMap[SourceRecordMethod().apply {
                    this.className = RegexUtil.replaceClassName(className)
                    this.methodName = RegexUtil.replaceMethodName(methodName)
                    this.methodDesc = RegexUtil.replaceMethodDesc(methodDesc)
                    Log.e("XJL", "className: ${this.className}, methodName: ${methodName}, methodDesc: ${methodDesc}")
                    this.extend = this@GDInjectCollectMethodVisitor.extend
                    this.supered = this@GDInjectCollectMethodVisitor.supered
                    this.needSourceInfo = this@GDInjectCollectMethodVisitor.needSourceInfo
                }] = beCallerMethod
            }
        }
    }

    inner class GDInjectAnnotationArrayVisitor() : AnnotationVisitor(AsmConstant.ASM_VERSION) {
        override fun visit(name: String?, value: Any?) {
            (value as? String)?.let {
                targets.add(it)
            }
        }
    }
}