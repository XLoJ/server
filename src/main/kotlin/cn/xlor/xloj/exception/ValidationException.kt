package cn.xlor.xloj.exception

import org.springframework.http.HttpStatus

class Violation(val field: String, val message: String)

class ValidationException(
  status: HttpStatus,
  message: String,
  val errors: List<Violation>
) : BaseExceptionResponse(status, message)
