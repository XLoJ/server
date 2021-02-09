package cn.xlor.xloj.exception

import org.springframework.http.HttpStatus

class IncorrectPasswordException(val username: String) : RuntimeException()

class IncorrectPasswordExceptionResponse(val username: String) :
  BaseExceptionResponse(HttpStatus.BAD_REQUEST, "用户名或密码错误")
