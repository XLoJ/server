package cn.xlor.xloj.contest.dto

import javax.validation.constraints.NotBlank

data class CreateContestDto(
  @field:NotBlank
  val name: String
)
