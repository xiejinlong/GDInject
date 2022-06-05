package com.xjl.inject.plugin.collect.intercept

import com.xjl.gdinject.annotation.Intercept
import com.xjl.gdinject.annotation.signature.AnnotationParam
import com.xjl.inject.plugin.collect.AbsCollectAnnotationVisitor
import com.xjl.inject.plugin.collect.BeHandlerMethod

class InterceptCollectAnnotationVisitor : AbsCollectAnnotationVisitor() {

    var supered: Boolean = false
    var extend: Boolean = false

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        when (name) {
            AnnotationParam.PARAM_SUPERED -> supered = (value as? Boolean) ?: false
            AnnotationParam.PARAM_EXTEND -> extend = (value as? Boolean) ?: false
        }

    }

    override fun supportAnnotation(): Class<*> {
        return Intercept::class.java
    }

    override fun generateBeHandlerMethod(): BeHandlerMethod {
        return InterceptBeHandlerMethod().apply {
            this.supered = this@InterceptCollectAnnotationVisitor.supered
            this.extend =  this@InterceptCollectAnnotationVisitor.extend
        }
    }
}