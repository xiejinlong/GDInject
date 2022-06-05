package com.xjl.inject.plugin.collect

import com.xjl.inject.plugin.collect.around.AroundCollectAnnotationVisitor
import com.xjl.inject.plugin.collect.intercept.InterceptCollectAnnotationVisitor
import com.xjl.inject.plugin.collect.replace.ReplaceCollectAnnotationVisitor
import com.xjl.inject.plugin.collect.trycatch.TryCacheCollectAnnotationVisitor

class CollectAnnotationVisitorChain {
    private val chain = mutableListOf<AbsCollectAnnotationVisitor>()

    init {
        chain.add(ReplaceCollectAnnotationVisitor())
        chain.add(AroundCollectAnnotationVisitor())
        chain.add(InterceptCollectAnnotationVisitor())
        chain.add(TryCacheCollectAnnotationVisitor())
    }

    fun proceedToVisitor(description: String): AbsCollectAnnotationVisitor? {
        return chain.firstOrNull { it.isSupportThisAnnotation(description) }
    }
}