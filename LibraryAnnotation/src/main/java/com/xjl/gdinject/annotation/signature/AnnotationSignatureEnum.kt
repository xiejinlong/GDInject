package com.xjl.gdinject.annotation.signature

import com.xjl.gdinject.annotation.Around
import com.xjl.gdinject.annotation.Inject
import com.xjl.gdinject.annotation.Replace
import com.xjl.gdinject.annotation.TryCatch

enum class AnnotationSignatureEnum(val any: Class<*>, val descriptor: String) {
    AnnotationReplace(Replace::class.java, "Lcom/xjl/gdinject/annotation/Replace;"),
    AnnotationTryCatch(TryCatch::class.java, "Lcom/xjl/gdinject/annotation/TryCatch;"),
    AnnotationInject(Inject::class.java, "Lcom/xjl/gdinject/annotation/Inject;"),
    AnnotationAround(Around::class.java, "Lcom/xjl/gdinject/annotation/Around;")

}