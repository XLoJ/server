package cn.xlor.xloj.repository

import cn.xlor.xloj.contest.dto.ContestWithWriter
import cn.xlor.xloj.model.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository

@Repository
class ContestRepository(
  private val database: Database,
  private val userRepository: UserRepository
) {
  @Cacheable(cacheNames = ["polygonContest"])
  fun polygonContest(): Contest {
    return database.contests.find { it.type eq Contest.PolygonType }!!
  }

  @Cacheable(cacheNames = ["LocalContest"])
  fun localContest(): Contest {
    return database.contests.find { it.type eq Contest.LocalType }!!
  }

  fun createContest(name: String, userId: Long): Long {
    return database.insertAndGenerateKey(Contests) {
      set(it.name, name)
      set(it.creator, userId)
    } as Long
  }

  fun updateContest(contestId: Long) {
    database.update(Contests) {
      where { it.id eq contestId }
    }
  }

  private fun filterBuiltinContest(): EntitySequence<Contest, Contests> {
    return database.contests
      .filter { it.type notEq Contest.PolygonType }
      .filter { it.type notEq Contest.LocalType }
      .filter { it.type notEq Contest.RemoteType }
  }

  fun findAllContests(): List<Contest> {
    return filterBuiltinContest()
      .sortedByDescending { it.id }.toList()
  }

  fun findAllPublicContests(): List<Contest> {
    return filterBuiltinContest().filter { it.public eq true }
      .sortedByDescending { it.id }.toList()
  }

  fun findAllUserCreateContests(uid: Long): List<Contest> {
    return filterBuiltinContest()
      .filter { it.public notEq true }
      .filter { it.creator eq uid }
      .sortedByDescending { it.id }.toList()
  }

  fun findAllUserManageContests(uid: Long): List<ContestWithWriter> {
    return database.from(Contests).innerJoin(
      ContestUsers,
      on = Contests.id eq ContestUsers.contest
    ).select(
      Contests.id,
      Contests.name,
      Contests.description,
      Contests.startTime,
      Contests.duration,
      Contests.creator,
      Contests.public,
      Contests.type,
      ContestUsers.user
    )
      .where { Contests.type notEq Contest.PolygonType }
      .where { Contests.type notEq Contest.LocalType }
      .where { Contests.type notEq Contest.RemoteType }
      .where { Contests.public eq false }
      .where { (ContestUsers.type eq ContestUser.WriterType) or (ContestUsers.type eq ContestUser.ManagerType) }
      .where { ContestUsers.user eq uid }
      .map { row ->
        val creator = userRepository.findOneUserById(row[Contests.creator]!!)!!
          .toUserProfile()
        val writers = findContestWritersById(row[Contests.id]!!)
        ContestWithWriter(
          id = row[Contests.id]!!,
          name = row[Contests.name]!!,
          description = row[Contests.description]!!,
          startTime = row[Contests.startTime]!!,
          duration = row[Contests.duration]!!,
          creator = creator,
          writers = listOf(creator) + writers,
          public = row[Contests.public]!!
        )
      }
  }

  fun findContestById(contestId: Long): Contest? {
    return database.contests.find { it.id eq contestId }
  }

  fun findContestWritersById(contestId: Long): List<UserProfile> {
    return database.contestUsers
      .filter { it.contest eq contestId }
      .filter { it.type eq ContestUser.WriterType }
      .sortedBy { it.id }
      .toList()
      .mapNotNull { userRepository.findOneUserById(it.user)?.toUserProfile() }
  }

  fun checkUserManageContest(contestId: Long, userId: Long): Boolean {
    return database.contestUsers
      .filter { it.contest eq contestId }
      .filter { it.user eq userId }
      .filter { (it.type eq ContestUser.ManagerType) or (it.type eq ContestUser.WriterType) }
      .count() > 0
  }
}
