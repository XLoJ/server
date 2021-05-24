package cn.xlor.xloj.repository

import cn.xlor.xloj.model.*
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.dsl.update
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.map
import org.ktorm.entity.toList
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProblemRepository(
  private val database: Database
) {
  fun findProblemById(pid: Long): Problem? {
    return database.problems.find { it.id eq pid }
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

  /**
   * Create a new classic problem
   *
   * Return: [Long] the primary key of new problem in Table Problems
   */
  @Transactional
  fun createClassicProblem(name: String, creatorId: Long): Long {
    val id = database.insertAndGenerateKey(Problems) {
      set(it.creatorId, creatorId)
      set(it.title, name)
      set(it.problemType, "classic")
    } as Long
    database.insert(ClassicProblems) {
      set(it.parent, id)
      set(it.name, name)
    }
    return id
  }

  fun updateProblemInfo(problem: Problem) {
    database.update(Problems) {
      set(it.timeLimit, problem.timeLimit)
      set(it.memoryLimit, problem.memoryLimit)
      set(it.tags, problem.tags)
      set(it.title, problem.title)
      set(it.legend, problem.legend)
      set(it.inputFormat, problem.inputFormat)
      set(it.outputFormat, problem.outputFormat)
      set(it.notes, problem.notes)
      where { it.id eq problem.id }
    }
  }

  fun setExamples(pid: Long, examples: String) {
    database.update(Problems) {
      set(it.examples, examples)
      where { it.id eq pid }
    }
  }

  fun setTestcaseNum(pid: Long, testcaseNum: Int) {
    database.update(Problems) {
      set(it.testcaseNum, testcaseNum)
      where { it.id eq pid }
    }
  }
}
