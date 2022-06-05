package com.xjl.gdinject.annotation.signature

class AnnotationParam {
    companion object {
        /**
         * @see com.xjl.gdinject.annotation.Replace
         */
        const val PARAM_TARGETS = "targets"
        /**
         * @see com.xjl.gdinject.annotation.Replace
         */
        const val PARAM_VERIFY = "forceVerify"
        /**
         * @see com.xjl.gdinject.annotation.Around
         */
        const val PARAM_AFTER = "after"
        /**
         * @see com.xjl.gdinject.annotation.Replace
         */
        const val PARAM_NEED_SOURCE_INFO = "needSourceInfo"
        /**
         * @see com.xjl.gdinject.annotation.TryCatch
         */
        const val PARAM_EXTEND = "extend"

        /**
         * @see com.xjl.gdinject.annotation.TryCatch
         */
        const val PARAM_SUPERED = "supered"

        /**
         *  黑名单，在黑名单中的不生效
         */
        const val PARAM_BLACK_LIST = "blackList"

        /**
         *  白名单，仅针对几个生效
         */
        const val PARAM_CALLER_LIST = "callerList"

    }
}