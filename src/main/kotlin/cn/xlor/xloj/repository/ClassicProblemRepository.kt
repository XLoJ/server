package cn.xlor.xloj.repository

import cn.xlor.xloj.model.ClassicProblem
import cn.xlor.xloj.model.ClassicProblems
import cn.xlor.xloj.model.classicProblems
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.update
import org.ktorm.entity.find
import org.springframework.stereotype.Repository

@Repository
class ClassicProblemRepository(
  private val database: Database
) {
  fun findClassicProblemByParentId(parentId: Long): ClassicProblem {
    return database.classicProblems.find { it.parent eq parentId }!!
  }

  fun updateClassicProblemTestcases(cpid: Long, testcases: String) {
    database.update(ClassicProblems) {
      set(it.testcases, testcases)
      where { it.id eq cpid }
    }
  }

  fun setClassicProblemChecker(cpid: Long, cid: Long) {
    database.update(ClassicProblems) {
      set(it.checker, cid)
      where { it.id eq cpid }
    }
  }

  fun setClassicProblemValidator(cpid: Long, cid: Long) {
    database.update(ClassicProblems) {
      set(it.validator, cid)
      where { it.id eq cpid }
    }
  }

  fun setClassicProblemSolution(cpid: Long, cid: Long) {
    database.update(ClassicProblems) {
      set(it.solution, cid)
      where { it.id eq cpid }
    }
  }
}
