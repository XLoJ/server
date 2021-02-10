package cn.xlor.xloj.security

import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse

fun makeUnAuthorizeResponse(
  response: ServletResponse,
  message: String,
  username: String = ""
) {
  val httpResponse = response as HttpServletResponse
  httpResponse.status = HttpStatus.UNAUTHORIZED.value()
  httpResponse.contentType = "application/json;charset=UTF-8"
  httpResponse.characterEncoding = "UTF-8"
  val responseBody =
    "{ \"status\": \"${HttpStatus.UNAUTHORIZED}\", \"timestamp\": \"${LocalDateTime.now()}\", \"message\": \"${message}\", \"username\": \"$username\" }"
  httpResponse.writer.println(responseBody)
}

fun makeNotFoundResponse(
  response: ServletResponse,
  message: String
) {
  val httpResponse = response as HttpServletResponse
  httpResponse.status = HttpStatus.NOT_FOUND.value()
  httpResponse.contentType = "application/json;charset=UTF-8"
  httpResponse.characterEncoding = "UTF-8"
  val responseBody =
    "{ \"status\": \"${HttpStatus.NOT_FOUND}\", \"timestamp\": \"${LocalDateTime.now()}\", \"message\": \"${message}\" }"
  httpResponse.writer.println(responseBody)
}

