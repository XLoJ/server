package cn.xlor.xloj.user.dto

import cn.xlor.xloj.model.UserGroup

data class UserRoleProfile(
  val id: Long,
  val username: String,
  val nickname: String,
  val groups: List<UserGroup>
)
