package com.xjl.inject.plugin.collect

import androidx.annotation.CallSuper
import com.kuaikan.library.libknifeasm.AsmConstant
import com.kuaikan.library.libknifeutil.util.ClassUtil
import com.kuaikan.library.libknifeutil.util.CloseUtil
import com.kuaikan.library.libknifeutil.util.Log
import com.kuaikan.library.libknifeutil.util.RegexUtil
import com.xjl.gdinject.annotation.signature.AnnotationParam
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Type

abstract class AbsCollectAnnotationVisitor:  AnnotationVisitor(AsmConstant.ASM_VERSION) {

    /**
     * 定义的被调用的方法
     */
    lateinit var beCallerMethod: BeCallerMethod

    /**
     * 注解标注的需要处理的方法
     */
    lateinit var annotationRecord: InjectAnnotationRecord

    var forceVerify: Boolean = true
    var targets: ArrayList<String> = arrayListOf()
    var blackList: ArrayList<String> = arrayListOf()
    var callerList: ArrayList<String> = arrayListOf()

    fun isSupportThisAnnotation(description: String): Boolean {
        return getDesc(supportAnnotation()) == description
    }

    private fun getDesc(clazz: Class<*>): String {
        return Type.getDescriptor(clazz)
    }


    @CallSuper
    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        when(name) {
            AnnotationParam.PARAM_VERIFY -> forceVerify = (value as? Boolean) ?: false
        }
    }

    /**
     * 共有参数, base类处理
     */
    @CallSuper
    override fun visitArray(name: String?): AnnotationVisitor {
        return when(name) {
            AnnotationParam.PARAM_TARGETS -> GDTargetAnnotationArrayVisitor()
            AnnotationParam.PARAM_BLACK_LIST -> GDBlackListAnnotationArrayVisitor()
            AnnotationParam.PARAM_CALLER_LIST -> GDCallerListAnnotationArrayVisitor()
            else -> super.visitArray(name)
        }
    }

    /**
     * parse param end.
     */
    @CallSuper
    override fun visitEnd() {
        super.visitEnd()
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
            val beHandlerMethod = generateBeHandlerMethod()
            beHandlerMethod.apply {
                this.className = RegexUtil.replaceClassName(className)
                this.methodName = RegexUtil.replaceMethodName(methodName)
                this.methodDesc = RegexUtil.replaceMethodDesc(methodDesc)
                this.blackList = this@AbsCollectAnnotationVisitor.blackList
                this.callerList = this@AbsCollectAnnotationVisitor.callerList
            }
            annotationRecord.injectMap[beHandlerMethod] = beCallerMethod
        }
    }

    /**
     * 子类需要复写这个方法
     */
    abstract fun supportAnnotation(): Class<*>
    abstract fun generateBeHandlerMethod(): BeHandlerMethod

    inner class GDTargetAnnotationArrayVisitor : AnnotationVisitor(AsmConstant.ASM_VERSION) {
        override fun visit(name: String?, value: Any?) {
            (value as? String)?.let {
                targets.add(it)
            }
        }
    }

    inner class GDBlackListAnnotationArrayVisitor : AnnotationVisitor(AsmConstant.ASM_VERSION) {
        override fun visit(name: String?, value: Any?) {
            (value as? String)?.let { v ->
                blackList.add(v)
            }
        }
    }

    inner class GDCallerListAnnotationArrayVisitor : AnnotationVisitor(AsmConstant.ASM_VERSION) {
        override fun visit(name: String?, value: Any?) {
            (value as? String)?.let { v ->
                callerList.add(v)
            }
        }
    }


}