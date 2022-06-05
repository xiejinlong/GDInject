package com.xjl.inject.plugin.proceed

import com.kuaikan.library.libknifeutil.util.CloseUtil
import com.xjl.inject.plugin.collect.BeHandlerMethod

/**
 * 黑名单处理
 */
object BlackListUtil {

    fun canDealThisMethod(className: String, beHandlerMethod: BeHandlerMethod): Boolean {
        //黑名单控制优先
        if (isInBlackList(className, beHandlerMethod)) {
            return false
        }
        if (beHandlerMethod.callerList.isEmpty()) {
            return true
        }
        // 再判断是否有配置caller， 配置了caller， 必须使用caller配置
        return isInCallerList(className, beHandlerMethod)
    }

    private fun isInCallerList(className: String, beHandlerMethod: BeHandlerMethod): Boolean {
        if (beHandlerMethod.callerList.isEmpty()) {
            return false
        }
        beHandlerMethod.callerList.forEach {
            val regex = Regex(it)
            val result = regex.matches(className!!)
            if (result) {
                return true
            }
        }
        return false

    }

    private fun isInBlackList(className: String, beHandlerMethod: BeHandlerMethod): Boolean {
        if (beHandlerMethod.blackList.isEmpty()) {
            return false
        }
        beHandlerMethod.blackList.forEach {
            val regex = Regex(it)
            val result = regex.matches(className)
            if (result) {
                return true
            }
        }
        return true
    }
}