package cn.xlor.xloj.problem.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

data class ClassicSubmissionDto(
  @field:NotBlank
  @field:NotEmpty
  val body: String,
  @field:NotBlank
  @field:NotEmpty
  val language: String,
)
