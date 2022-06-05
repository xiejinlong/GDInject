package com.xjl.inject.plugin.collect.around

import com.xjl.gdinject.annotation.Around
import com.xjl.gdinject.annotation.signature.AnnotationParam
import com.xjl.inject.plugin.collect.AbsCollectAnnotationVisitor
import com.xjl.inject.plugin.collect.BeHandlerMethod

class AroundCollectAnnotationVisitor : AbsCollectAnnotationVisitor() {


    var needSourceInfo: Boolean = false
    var supered: Boolean = false
    var after: Boolean = false
    var extend: Boolean = false

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        when (name) {
            AnnotationParam.PARAM_NEED_SOURCE_INFO -> needSourceInfo = (value as? Boolean) ?: false
            AnnotationParam.PARAM_SUPERED -> supered = (value as? Boolean) ?: false
            AnnotationParam.PARAM_AFTER -> after = (value as? Boolean) ?: false
            AnnotationParam.PARAM_EXTEND -> extend = (value as? Boolean) ?: false
        }

    }

    override fun supportAnnotation(): Class<*> {
        return Around::class.java
    }

    override fun generateBeHandlerMethod(): BeHandlerMethod {
        return AroundBeHandlerMethod().apply {
            this.needSourceInfo = this@AroundCollectAnnotationVisitor.needSourceInfo
            this.after = this@AroundCollectAnnotationVisitor.after
            this.supered = this@AroundCollectAnnotationVisitor.supered
            this.extend =  this@AroundCollectAnnotationVisitor.extend
        }
    }
}