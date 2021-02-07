package cn.xlor.oj.repository

import cn.xlor.oj.model.User
import cn.xlor.oj.model.users
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
  private val database: Database
) {
  fun findOneUserById(id: Int): User? {
    return database.users.find { it.id eq id }
  }

  fun findOneUserByUsername(username: String): User? {
    return database.users.find { it.username eq username }
  }
}
