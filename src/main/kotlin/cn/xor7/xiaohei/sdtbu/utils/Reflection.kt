package cn.xor7.xiaohei.sdtbu.utils

import java.lang.reflect.Field
import kotlin.reflect.KClass


class JavaFieldProperty<R>(private val javaField: Field) {
    init {
        javaField.isAccessible = true
    }

    @Suppress("UNCHECKED_CAST")
    fun get(receiver: Any): R {
        return javaField.get(receiver) as R
    }

    fun set(receiver: Any, value: R) {
        javaField.set(receiver, value)
    }
}

fun <R> accessField(targetClass: KClass<*>, fieldName: String): JavaFieldProperty<R> {
    val javaField: Field
    val targetJClass = targetClass.java
    try {
        javaField = targetJClass.getDeclaredField(fieldName)
    } catch (e: NoSuchFieldException) {
        throw IllegalArgumentException("Field '$fieldName' not found in class ${targetJClass.name}", e)
    }
    return JavaFieldProperty(javaField)
}