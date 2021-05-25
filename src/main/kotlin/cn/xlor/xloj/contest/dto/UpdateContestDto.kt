package cn.xlor.xloj.contest.dto

import javax.validation.constraints.NotBlank

data class UpdateContestDto(
  @field:NotBlank
  val name: String?,
  @field:NotBlank
  val startTime: String?,
  val description: String?,
  val duration: Int?
)
