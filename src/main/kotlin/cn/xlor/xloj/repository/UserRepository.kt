package cn.xlor.xloj.repository

import cn.xlor.xloj.model.User
import cn.xlor.xloj.model.Users
import cn.xlor.xloj.model.users
import cn.xlor.xloj.user.dto.UserRegisterDto
import cn.xlor.xloj.security.hashPassword
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.find
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
  private val database: Database
) {
  fun createUser(userRegisterDto: UserRegisterDto) {
    database.insert(Users) {
      set(it.username, userRegisterDto.username)
      set(it.password, hashPassword(userRegisterDto.password))
      set(it.nickname, userRegisterDto.nickname)
    }
  }

  fun findOneUserById(id: Int): User? {
    return database.users.find { it.id eq id }
  }

  fun findOneUserByUsername(username: String): User? {
    return database.users.find { it.username eq username }
  }
}
