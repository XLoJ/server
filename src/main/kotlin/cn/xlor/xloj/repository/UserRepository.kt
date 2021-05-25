package cn.xlor.xloj.repository

import cn.xlor.xloj.model.*
import cn.xlor.xloj.security.hashPassword
import cn.xlor.xloj.user.dto.UserRegisterDto
import cn.xlor.xloj.utils.LoggerDelegate
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.toList
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
  private val database: Database
) {
  private val logger by LoggerDelegate()

  fun createUser(userRegisterDto: UserRegisterDto) {
    database.insert(Users) {
      set(it.username, userRegisterDto.username)
      set(it.password, hashPassword(userRegisterDto.password))
      set(it.nickname, userRegisterDto.nickname)
    }
  }

  fun findOneUserById(id: Long): User? {
    return database.users.find { it.id eq id }
  }

  fun findOneUserByUsername(username: String): User? {
    return database.users.find { it.username eq username }
  }

  fun findUserGroups(uid: Long): List<UserGroup> {
    return database.userGroups.filter { it.uid eq uid }.toList()
  }

  fun isUserAdmin(uid: Long): Boolean {
    return database.userGroups.find { (it.uid eq uid) and (it.group eq adminGroup().id) } != null
  }

  /*
   * Created in database
   */
  @Cacheable(cacheNames = ["adminGroup"])
  fun adminGroup(): Group {
    logger.debug("Query admin...")
    return database.groups.find { it.name eq "admin" }!!
  }

  /*
   * Created in database
   */
  @Cacheable(cacheNames = ["polygonGroup"])
  fun polygonGroup(): Group {
    logger.debug("Query polygon...")
    return database.groups.find { it.name eq "polygon" }!!
  }
}
