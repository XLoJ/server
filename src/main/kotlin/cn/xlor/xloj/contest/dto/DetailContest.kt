package cn.xlor.xloj.contest.dto

import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.model.UserProfile
import java.time.Instant

data class DetailContest(
  val id: Long,
  val name: String,
  val description: String,
  val startTime: Instant,
  val duration: Int,
  val creator: UserProfile,
  val writers: List<UserProfile>,
  val problems: List<Problem>
)