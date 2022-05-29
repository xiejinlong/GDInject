package com.xjl.inject.plugin.proceed

import com.kuaikan.library.libknifeutil.util.ClassUtil
import com.kuaikan.library.libknifeutil.util.CloseUtil
import com.xjl.inject.plugin.collect.BeCallerMethod
import com.xjl.inject.plugin.collect.SourceRecordMethod
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * 在Replace与Around注解的使用过程中，都需要保证当前定义的参数列表与原方法一致
 * 这个工具类用来校验当前方法是否与原方法一致
 * @see com.xjl.gdinject.annotation.Replace
 */
object ParamCheckUtil {


    fun checkParamValid(
        sourceRecordMethod: SourceRecordMethod,
        beCallerMethod: BeCallerMethod
    ) {
        try {
            checkParamCount(sourceRecordMethod, beCallerMethod)
            checkParamType(sourceRecordMethod, beCallerMethod)
        } catch (e: Exception) {
            e.printStackTrace()
            CloseUtil.exit("exception when checkParamType!, ${e.message}")

        }

    }

    private fun checkParamType(
        sourceRecordMethod: SourceRecordMethod,
        beCallerMethod: BeCallerMethod
    ) {


        var beCallerMethodStartCount = 0
        val injectMethodRecordType: Array<Type> =
            Type.getArgumentTypes(sourceRecordMethod.methodDesc)
        val beCallMethodParamTypes: Array<Type> =
            Type.getArgumentTypes(beCallerMethod.methodDescriptor)
        //首先check第一个参数
        if (sourceRecordMethod.opcode == Opcodes.INVOKEVIRTUAL
            || sourceRecordMethod.opcode == Opcodes.INVOKEINTERFACE
            || sourceRecordMethod.opcode == Opcodes.INVOKESPECIAL
            || !sourceRecordMethod.isStatic
        ) {
            val ownerClassName: String = sourceRecordMethod.className ?: ""
            val type: Type = beCallMethodParamTypes[0]
            val firstParamClassName = type.className
            if (!ClassUtil.isSuper(ownerClassName, firstParamClassName)) {
                CloseUtil.exit("$firstParamClassName is a subclass of $ownerClassName or itself! ")
            }
            beCallerMethodStartCount++
        }
        // 接下来按照参数逐个check
        injectMethodRecordType.forEach {
            val injectSourceClassName = it.className
            val beCallerMethodClassName =
                beCallMethodParamTypes[beCallerMethodStartCount].className
            if (injectSourceClassName != beCallerMethodClassName) {
                CloseUtil.exit("param does no the same, injectSourceClassName: $injectSourceClassName, beCallMethodClassName: $beCallerMethodClassName")
            }
            beCallerMethodStartCount++
        }
        if (sourceRecordMethod.needSourceInfo) {
            val needSourceInfoClassName = beCallMethodParamTypes[beCallerMethodStartCount].className
            if (String::class.java.name != needSourceInfoClassName) {
                CloseUtil.exit("source type is error, need string, but now is $needSourceInfoClassName ")
            }
        }
    }

    /**
     * 如果是插入静态方法，那么参数列表需要一致
     * 如果是插入普通方法，那么定义的被调用的参数需要多一个
     */
    private fun checkParamCount(
        sourceRecordMethod: SourceRecordMethod,
        beCallerMethod: BeCallerMethod
    ) {
        val injectMethodParamCount: Int = Type.getArgumentTypes(sourceRecordMethod.methodDesc).size
        val beCallMethodParamCount: Int =
            Type.getArgumentTypes(beCallerMethod.methodDescriptor).size
        val needSourceInfo: Boolean = sourceRecordMethod.needSourceInfo
        val targetRealParamCount = beCallMethodParamCount - if (needSourceInfo) 1 else 0
        when (sourceRecordMethod.opcode) {
            Opcodes.INVOKESTATIC -> {
                if (injectMethodParamCount != targetRealParamCount) {
                    CloseUtil.exit("illegal param count, target: $targetRealParamCount, injectCount: $injectMethodParamCount")
                }
            }
            Opcodes.INVOKEVIRTUAL -> {
                if (injectMethodParamCount + 1 != targetRealParamCount) {
                    CloseUtil.exit("illegal param count, target: $targetRealParamCount, injectCount: $injectMethodParamCount")
                }
            }
        }
    }
}