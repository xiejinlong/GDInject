package com.xjl.inject.plugin.collect

class InjectMethodRecord {
    var className: String? = null
    var methodName: String? = null
    var methodDesc: String? = null
    var opcode: Int = 0
    var extend: Boolean = false
    var supered: Boolean = false
    var needSourceInfo: Boolean = false

    override fun equals(other: Any?): Boolean {
        if (other !is InjectMethodRecord) return false
        if (other.className != this.className) {
            return false
        }
        if (other.methodName != this.methodName) {
            return false
        }
        if (other.methodDesc != this.methodDesc) {
            return false
        }
        if (other.extend != this.extend) {
            return false
        }
        if (other.supered != this.supered) {
            return false
        }
        return true
    }
}