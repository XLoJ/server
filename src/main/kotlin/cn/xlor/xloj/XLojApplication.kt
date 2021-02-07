package cn.xlor.xloj

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class XLojApplication

fun main(args: Array<String>) {
  runApplication<XLojApplication>(*args)
}
