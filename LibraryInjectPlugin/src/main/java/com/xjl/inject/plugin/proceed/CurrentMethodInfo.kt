package com.xjl.inject.plugin.proceed

/**
 * 用来记录当前方法调用的信息
 */
class CurrentMethodInfo {
    var className: String? = null
    var methodName: String? = null
    var methodDesc: String? = null
    var methodAccess: Int = 0
}