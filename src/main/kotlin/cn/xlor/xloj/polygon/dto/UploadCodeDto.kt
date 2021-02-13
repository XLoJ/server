package cn.xlor.xloj.polygon.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class UploadCodeDto(
  @field:NotBlank()
  val body: String,
  @field:NotBlank()
  @field:Size(min = 1, max = 64)
  val language: String,
  @field:NotBlank()
  @field:Size(min = 1, max = 64)
  val name: String,
  val description: String
)
