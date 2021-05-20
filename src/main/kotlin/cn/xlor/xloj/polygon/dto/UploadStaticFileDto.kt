package cn.xlor.xloj.polygon.dto

import javax.validation.constraints.NotBlank

data class UploadStaticFileDto(
  @field:NotBlank()
  val filename: String,
  @field:NotBlank()
  val body: String
)
