package cn.xlor.xloj.contest.dto

import cn.xlor.xloj.model.Contest
import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.model.UserProfile
import java.time.Instant

data class SubmissionSummary(
  val id: Long,
  val user: UserProfile,
  val contest: Contest,
  val problem: Problem,
  val language: String,
  val verdict: Int,
  val time: Int,
  val memory: Double,
  val pass: Int,
  val from: String,
  val createTime: Instant
)
