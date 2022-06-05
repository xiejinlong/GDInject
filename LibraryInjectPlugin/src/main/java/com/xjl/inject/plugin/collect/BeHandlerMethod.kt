package com.xjl.inject.plugin.collect

open class BeHandlerMethod {
    var className: String? = null
    var methodName: String? = null
    var methodDesc: String? = null
    var opcode: Int = 0
    var isStatic = false
    var blackList: List<String> = mutableListOf()
    var callerList: List<String> = mutableListOf()

    open fun isNeedSourceInfo(): Boolean {
        return false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BeHandlerMethod) return false
        if (other.className != this.className) {
            return false
        }
        if (other.methodName != this.methodName) {
            return false
        }
        if (other.methodDesc != this.methodDesc) {
            return false
        }
        return true
    }
}