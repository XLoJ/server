package cn.xlor.xloj.problem.dto

import cn.xlor.xloj.model.Contest
import cn.xlor.xloj.model.UserProfile
import java.time.Instant

data class DetailClassicSubmission(
  val id: Long,
  val user: UserProfile,
  val contest: Contest,
  val problem: Long,
  val body: String,
  val language: String,
  val verdict: Int,
  val time: Double,
  val memory: Double,
  val pass: Int,
  val createTime: Instant,
  val messages: List<Any>
)