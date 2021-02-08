package cn.xlor.xloj.user.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class UserRegisterDto(
  @field:NotBlank()
  @field:Size(min = 3, max = 16)
  val username: String,
  @field:NotBlank()
  @field:Size(min = 3, max = 16)
  val password: String,
  @field:NotBlank()
  @field:Size(min = 3, max = 16)
  val nickname: String
)
