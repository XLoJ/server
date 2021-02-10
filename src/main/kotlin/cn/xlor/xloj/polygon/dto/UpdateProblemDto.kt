package cn.xlor.xloj.polygon.dto

import javax.validation.constraints.Max
import javax.validation.constraints.Min

data class UpdateProblemDto(
  @field:Min(1000)
  @field:Max(1000 * 15)
  val timeLimit: Int?,
  @field:Min(32)
  @field:Max(2048)
  val memoryLimit: Int?,
  val tags: String?,
  val title: String?,
  val legend: String?,
  val inputFormat: String?,
  val outputFormat: String?,
  val notes: String?
)
