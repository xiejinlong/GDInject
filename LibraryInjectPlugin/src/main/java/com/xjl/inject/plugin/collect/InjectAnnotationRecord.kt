package com.xjl.inject.plugin.collect

class InjectAnnotationRecord {
    var annotationSignature: Class<*>? = null
    var injectMap = hashMapOf<BeHandlerMethod, BeCallerMethod>()


}