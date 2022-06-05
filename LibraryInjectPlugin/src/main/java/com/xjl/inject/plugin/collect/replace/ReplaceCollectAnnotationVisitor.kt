package com.xjl.inject.plugin.collect.replace

import com.xjl.gdinject.annotation.Replace
import com.xjl.gdinject.annotation.signature.AnnotationParam
import com.xjl.inject.plugin.collect.AbsCollectAnnotationVisitor
import com.xjl.inject.plugin.collect.BeHandlerMethod

class ReplaceCollectAnnotationVisitor : AbsCollectAnnotationVisitor() {


    var needSourceInfo: Boolean = false


    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        when (name) {
            AnnotationParam.PARAM_NEED_SOURCE_INFO -> needSourceInfo = (value as? Boolean) ?: false
        }

    }
    override fun supportAnnotation(): Class<*> {
        return Replace::class.java
    }

    override fun generateBeHandlerMethod(): BeHandlerMethod {
        return ReplaceBeHandlerMethod().apply {
            this.needSourceInfo = this@ReplaceCollectAnnotationVisitor.needSourceInfo
        }
    }
}