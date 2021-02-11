package cn.xlor.xloj.polygon.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class CreateProblemDto(
  @field:NotBlank()
  @Size(max = 32)
  val name: String,
  @field:NotBlank()
  @Size(max = 16)
  val type: String
)
