package cn.xlor.xloj.exception

import org.springframework.http.HttpStatus

class UnAuthorizeException(message: String) : RuntimeException(message)

class UnAuthorizeExceptionResponse(message: String) :
  BaseExceptionResponse(HttpStatus.UNAUTHORIZED, message)
