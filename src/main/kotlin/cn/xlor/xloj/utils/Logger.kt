package cn.xlor.oj.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.companionObject

class LoggerDelegate<in R : Any> : ReadOnlyProperty<R, Logger> {
  private var logger: Logger? = null

  override fun getValue(thisRef: R, property: KProperty<*>): Logger {
    if (logger == null) logger =
      getLogger(getClassForLogging(thisRef.javaClass))
    return logger!!
  }
}

fun getLogger(forClass: Class<*>): Logger = LoggerFactory.getLogger(forClass)

fun <T : Any> getClassForLogging(javaClass: Class<T>): Class<*> {
  return javaClass.enclosingClass?.takeIf {
    it.kotlin.companionObject?.java == javaClass
  } ?: javaClass
}
