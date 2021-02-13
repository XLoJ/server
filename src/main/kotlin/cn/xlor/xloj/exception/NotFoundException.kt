package cn.xlor.xloj.exception

import org.springframework.http.HttpStatus

class NotFoundException(message: String) : RuntimeException(message)

class NotFoundExceptionResponse(message: String) :
  BaseExceptionResponse(HttpStatus.NOT_FOUND, message)
