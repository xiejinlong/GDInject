package com.xjl.inject.plugin.collect.intercept

import com.xjl.inject.plugin.collect.BeHandlerMethod

class InterceptBeHandlerMethod: BeHandlerMethod() {
    var supered: Boolean = false
    var extend: Boolean = false
}