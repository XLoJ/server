package cn.xlor.xloj.repository

import cn.xlor.xloj.model.ClassicProblem
import cn.xlor.xloj.model.classicProblems
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.springframework.stereotype.Repository

@Repository
class ClassicProblemRepository(
  private val database: Database
) {
  fun findClassicProblemByParentId(parentId: Long): ClassicProblem {
    return database.classicProblems.find { it.parent eq parentId }!!
  }
}
