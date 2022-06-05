package com.xjl.inject.plugin.collect.around

import com.xjl.inject.plugin.collect.BeHandlerMethod

class AroundBeHandlerMethod: BeHandlerMethod() {
    var needSourceInfo: Boolean = false
    var after: Boolean = false
    var supered: Boolean = false
    var extend: Boolean = false

    override fun isNeedSourceInfo(): Boolean {
        return needSourceInfo
    }
}