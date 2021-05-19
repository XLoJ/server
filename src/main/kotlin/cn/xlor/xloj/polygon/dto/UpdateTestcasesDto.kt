package cn.xlor.xloj.polygon.dto

import javax.validation.constraints.NotBlank

data class UpdateTestcasesDto(
  @field:NotBlank()
  val testcases: String
)
