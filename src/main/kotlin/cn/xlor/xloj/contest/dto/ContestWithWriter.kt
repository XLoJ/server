package cn.xlor.xloj.contest.dto

import cn.xlor.xloj.model.UserProfile
import java.time.Instant

data class ContestWithWriter(
  val id: Long,
  val name: String,
  val description: String,
  val startTime: Instant,
  val duration: Int,
  val creator: UserProfile,
  val writers: List<UserProfile>,
  val public: Boolean
)
