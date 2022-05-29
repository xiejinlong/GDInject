package com.xjl.inject.plugin.collect

import java.lang.IllegalArgumentException
import java.util.concurrent.ConcurrentHashMap

object GlobalCollectorContainer {
    val collectMap = ConcurrentHashMap<String, InjectAnnotationRecord>()

    fun restData() {
        collectMap.clear()
    }

    @Synchronized
    fun getOrCreateByAnnotationSignature(annotationSignature: String?): InjectAnnotationRecord {
        annotationSignature ?: throw IllegalArgumentException("can find annotationSignature: $annotationSignature")
        var injectAnnotationRecord = collectMap[annotationSignature]
        if (injectAnnotationRecord == null) {
            injectAnnotationRecord = InjectAnnotationRecord().apply {
                this.annotationSignature = annotationSignature
            }
            collectMap[annotationSignature] = injectAnnotationRecord
        }
        return injectAnnotationRecord
    }
}