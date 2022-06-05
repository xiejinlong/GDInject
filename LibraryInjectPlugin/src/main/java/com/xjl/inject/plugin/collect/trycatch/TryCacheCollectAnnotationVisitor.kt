package com.xjl.inject.plugin.collect.trycatch

import com.xjl.gdinject.annotation.Around
import com.xjl.gdinject.annotation.TryCatch
import com.xjl.gdinject.annotation.signature.AnnotationParam
import com.xjl.inject.plugin.collect.AbsCollectAnnotationVisitor
import com.xjl.inject.plugin.collect.BeHandlerMethod
import com.xjl.inject.plugin.collect.around.AroundBeHandlerMethod

class TryCacheCollectAnnotationVisitor : AbsCollectAnnotationVisitor() {


    var needSourceInfo: Boolean = false
    var supered: Boolean = false
    var extend: Boolean = false

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        when (name) {
            AnnotationParam.PARAM_NEED_SOURCE_INFO -> needSourceInfo = (value as? Boolean) ?: false
            AnnotationParam.PARAM_SUPERED -> supered = (value as? Boolean) ?: false
            AnnotationParam.PARAM_EXTEND -> extend = (value as? Boolean) ?: false
        }

    }

    override fun supportAnnotation(): Class<*> {
        return TryCatch::class.java
    }

    override fun generateBeHandlerMethod(): BeHandlerMethod {
        return TryCatchBeHandlerMethod().apply {
            this.extend = this@TryCacheCollectAnnotationVisitor.extend
            this.supered = this@TryCacheCollectAnnotationVisitor.supered
        }
    }
}