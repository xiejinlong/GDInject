package com.xjl.inject.plugin.collect.replace

import com.xjl.inject.plugin.collect.BeHandlerMethod

class ReplaceBeHandlerMethod: BeHandlerMethod() {
    /**
     * 是否需要来源记录
     */
    var needSourceInfo: Boolean = false

    override fun isNeedSourceInfo(): Boolean {
        return needSourceInfo
    }
}