package cn.xlor.xloj.exception

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

open class BaseExceptionResponse(val status: HttpStatus, val message: String) {
  val timestamp: LocalDateTime = LocalDateTime.now()
}
