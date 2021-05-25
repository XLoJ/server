package cn.xlor.xloj.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int
import org.ktorm.schema.long

interface ContestProblem : Entity<ContestProblem> {
  companion object : Entity.Factory<ContestProblem>()

  val id: Long

  val contest: Contest

  val problem: Problem

  val index: Int

  val visible: Boolean

  var passCount: Int

  var subCount: Int
}

object ContestProblems : Table<ContestProblem>("contest__problems") {
  val id = long("id").primaryKey().bindTo { it.id }

  val contest = long("contest").references(Contests) { it.contest }

  val problem = long("problem").references(Problems) { it.problem }

  val index = int("index").bindTo { it.index }

  val visible = boolean("visible").bindTo { it.visible }

  val passCount = int("pass_count").bindTo { it.passCount }

  val subCount = int("sub_count").bindTo { it.subCount }
}

val Database.contestProblems get() = this.sequenceOf(ContestProblems)
