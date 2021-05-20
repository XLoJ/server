package cn.xlor.xloj.exception

import org.springframework.http.HttpStatus

class BadRequestException(message: String) : RuntimeException(message)

class BadRequestExceptionResponse(message: String) :
  BaseExceptionResponse(HttpStatus.BAD_REQUEST, message)
