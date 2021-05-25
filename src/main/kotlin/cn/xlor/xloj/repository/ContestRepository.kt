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

  fun isBuiltinContest(contest: Contest): Boolean {
    return if (contest.id == polygonContest().id || contest.id == localContest().id) {
      true
    } else contest.type == Contest.RemoteType
  }

  fun createContest(name: String, userId: Long): Long {
    return database.insertAndGenerateKey(Contests) {
      set(it.name, name)
      set(it.creator, userId)
    } as Long
  }

  fun updateContest(contestId: Long, contest: Contest) {
    database.update(Contests) {
      set(it.name, contest.name)
      set(it.description, contest.description)
      set(it.startTime, contest.startTime)
      set(it.duration, contest.duration)
      where { it.id eq contestId }
    }
  }

  fun updateContestPublic(contestId: Long, public: Boolean) {
    database.update(Contests) {
      set(it.public, public)
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

  // --- Problem ---
  fun findVisibleContestProblems(contestId: Long): List<ContestProblem> {
    return database.contestProblems.filter { it.contest eq contestId }
      .filter { it.visible eq true }
      .sortedBy { it.index }.toList()
  }

  fun findAllContestProblems(contestId: Long): List<ContestProblem> {
    return database.contestProblems.filter { it.contest eq contestId }
      .sortedBy { it.index }.toList()
  }

  fun pushContestProblem(contestId: Long, problemId: Long): ContestProblem {
    var curIndex = 0
    for ((index, contestProblem) in findAllContestProblems(contestId).withIndex()) {
      if (index != contestProblem.index) {
        curIndex = index
        break
      }
    }
    val cpId = database.insertAndGenerateKey(ContestProblems) {
      set(it.contest, contestId)
      set(it.problem, problemId)
      set(it.index, curIndex)
      set(it.visible, false)
    } as Long
    return database.contestProblems.find { it.id eq cpId }!!
  }

  fun updateContestProblemIndex(cpId: Long, index: Int) {
    database.update(ContestProblems) {
      set(it.index, index)
      where { it.id eq cpId }
    }
  }

  fun updateContestProblem(cpId: Long, problemId: Long) {
    database.update(ContestProblems) {
      set(it.problem, problemId)
      where { it.id eq cpId }
    }
  }

  fun setContestProblemVisible(cpId: Long, visible: Boolean) {
    database.update(ContestProblems) {
      set(it.visible, visible)
      where { it.id eq cpId }
    }
  }

  fun removeContestProblem(cpId: Long) {
    database.delete(ContestProblems) { it.id eq cpId }
  }
}
