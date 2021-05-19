package cn.xlor.xloj.user.dto

import cn.xlor.xloj.model.Group

data class UserRoleProfile(
  val id: Long,
  val username: String,
  val nickname: String,
  val groups: List<Group>
)
