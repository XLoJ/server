package cn.xlor.xloj.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text

interface User : Entity<User> {
  companion object : Entity.Factory<User>()

  var id: Int

  var username: String

  var nickname: String

  var password: String
}

object Users : Table<User>("users") {
  val id = int("id").primaryKey().bindTo { it.id }

  val username = text("username").bindTo { it.username }

  val nickname = text("nickname").bindTo { it.nickname }

  val password = text("password").bindTo { it.password }
}

val Database.users get() = this.sequenceOf(Users)
