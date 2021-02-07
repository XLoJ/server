package cn.xlor.xloj.exception

import cn.xlor.xloj.utils.LoggerDelegate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException

@RestControllerAdvice
class ExceptionAdvice {

  private val logger by LoggerDelegate()

  @ExceptionHandler(value = [Throwable::class])
  fun handleAll(throwable: Throwable): ResponseEntity<BaseExceptionResponse> {
    logger.error(throwable.message)
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(BaseExceptionResponse(HttpStatus.BAD_REQUEST, throwable.message ?: "Unknown"))
  }

  @ExceptionHandler(value = [MethodArgumentNotValidException::class])
  fun handleMethodArgumentNotValid(exception: MethodArgumentNotValidException): ResponseEntity<BaseExceptionResponse> {
    val violations = exception.bindingResult.allErrors
      .mapNotNull { error ->
        when (error) {
          is FieldError -> Violation(
            error.field, error.defaultMessage
              ?: "Field Error"
          )
          is ObjectError -> Violation(
            error.objectName, error.defaultMessage
              ?: "Object Error"
          )
          else -> null
        }
      }
      .toList()
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(
        ValidationException(
          HttpStatus.BAD_REQUEST, exception.message, violations
        )
      )
  }

  @ExceptionHandler(value = [WebExchangeBindException::class])
  fun handleMethodArgumentNotValid(exception: WebExchangeBindException): ResponseEntity<ValidationException> {
    val violations = exception.bindingResult.allErrors
      .mapNotNull { error ->
        when (error) {
          is FieldError -> Violation(
            error.field, error.defaultMessage
              ?: "Field Error"
          )
          is ObjectError -> Violation(
            error.objectName, error.defaultMessage
              ?: "Object Error"
          )
          else -> null
        }
      }
      .toList()
    return ResponseEntity
      .status(exception.status)
      .body(
        ValidationException(
          exception.status, exception.reason
            ?: "Validation failure", violations
        )
      )
  }
}
