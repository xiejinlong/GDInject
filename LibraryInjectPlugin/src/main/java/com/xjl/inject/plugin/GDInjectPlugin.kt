package com.xjl.inject.plugin

import com.android.build.api.transform.Status
import com.kuaikan.library.classknife.KBaseClassKnifePlugin
import com.kuaikan.library.libknifebase.core.*
import com.xjl.inject.plugin.collect.GDInjectCollectClassVisitor
import com.xjl.inject.plugin.collect.GlobalCollectorContainer
import com.xjl.inject.plugin.proceed.GDInjectProceedVisitor
import org.objectweb.asm.ClassVisitor

class GDInjectPlugin: KBaseClassKnifePlugin() {
    override fun enableIncremental(): Boolean {
        return true
    }
    override fun getKnifeCodeType(): KnifeCodeType {
        return KnifeCodeType.VISITOR
    }

    override fun getTransformHacker(): ITransformHacker? {
        return object : TransformHackerAdapter() {
            override fun beforeTransform() {
                GlobalCollectorContainer.restData()
            }
        }
    }

    override fun getTransformer(): ITransform {
        return VisitorTransformer().apply {
            this.getKnifeTransform = object : ITransformByClassVisitor {
                override fun generateClassVisitor(originClassVisitor: ClassVisitor, status: Status): ClassVisitor {
                    return GDInjectProceedVisitor(originClassVisitor)
                }
            }
            this.getCollectTransformer = object : ITransformByClassVisitor {
                override fun generateClassVisitor(originClassVisitor: ClassVisitor, status: Status): ClassVisitor {
                    return GDInjectCollectClassVisitor(originClassVisitor)
                }

            }
        }
    }
}