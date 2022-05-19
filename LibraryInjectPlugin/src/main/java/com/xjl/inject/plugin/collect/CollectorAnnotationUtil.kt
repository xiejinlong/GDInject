package com.xjl.inject.plugin.collect

import com.xjl.gdinject.annotation.Replace
import com.xjl.gdinject.annotation.TryCatch
import org.objectweb.asm.Type

class CollectorAnnotationUtil {
    companion object {
        fun shouldCollectorSource(desc: String?): Boolean {
            return when (desc) {
                getDesc(TryCatch::class.java),
                getDesc(Replace::class.java) -> {
                    true
                }
                else -> {
                    false
                }
            }
        }

        private fun getDesc(clazz: Class<*>): String {
            return Type.getDescriptor(clazz)
        }
    }
}