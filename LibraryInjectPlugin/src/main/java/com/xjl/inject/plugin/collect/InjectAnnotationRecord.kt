package com.xjl.inject.plugin.collect

class InjectAnnotationRecord {
    var annotationSignature: String? = null
    var injectMap = hashMapOf<InjectMethodRecord, BeCallerMethod>()


}