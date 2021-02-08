package cn.xlor.xloj.exception

import org.springframework.http.HttpStatus

class UnknownUserException(val username: String) : RuntimeException()

class UnknownUserExceptionResponse(val username: String) :
  BaseExceptionResponse(HttpStatus.NOT_FOUND, "用户名 \"$username\" 不存在")
