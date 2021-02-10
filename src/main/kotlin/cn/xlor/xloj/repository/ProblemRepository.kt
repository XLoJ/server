package cn.xlor.xloj.repository

import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.model.problems
import cn.xlor.xloj.model.userProblems
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.map
import org.ktorm.entity.toList
import org.springframework.stereotype.Repository

@Repository
class ProblemRepository(
  private val database: Database
) {
  fun findUserCreateProblem(uid: Long, pid: Long): Problem? {
    return database.problems.find { (it.id eq pid) and (it.creatorId eq uid) }
  }

  fun findUserCreateProblemList(uid: Long): List<Problem> {
    return database.problems.filter { it.creatorId eq uid }.toList()
  }

  fun findUserProblemList(uid: Long): List<Problem> {
    val createProblemList = findUserCreateProblemList(uid)
    val accessProblemList =
      database.userProblems.filter { it.uid eq uid }.map { it.problem }.toList()
    return (createProblemList + accessProblemList).sortedByDescending { it.updateTime }
  }
}
