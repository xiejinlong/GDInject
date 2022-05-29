package com.xjl.inject.plugin.collect

class SourceRecordMethod {
    var className: String? = null
    var methodName: String? = null
    var methodDesc: String? = null
    var opcode: Int = 0
    var isStatic = true
    var extend: Boolean = false
    var supered: Boolean = false
    var needSourceInfo: Boolean = false
    var after: Boolean = false

    override fun equals(other: Any?): Boolean {
        if (other !is SourceRecordMethod) return false
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