package cn.xlor.xloj.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.Instant

interface ClassicProblem : Entity<ClassicProblem> {
  companion object : Entity.Factory<ClassicProblem>()

  val id: Long

  val parent: Long

  val name: String

  var status: Int

  var checker: Long?

  var validator: Long?

  var solution: Long?

  var version: Int

  val createTime: Instant

  val updateTime: Instant
}

object ClassicProblems : Table<ClassicProblem>("classic_problems") {
  val id = long("id").primaryKey().bindTo { it.id }

  val parent = long("parent").bindTo { it.parent }

  val name = text("name").bindTo { it.name }

  val status = int("status").bindTo { it.status }

  val checker = long("checker").bindTo { it.checker }

  val validator = long("validator").bindTo { it.validator }

  val solution = long("solution").bindTo { it.solution }

  val version = int("version").bindTo { it.version }

  val createTime = timestamp("create_time").bindTo { it.createTime }

  val updateTime = timestamp("update_time").bindTo { it.updateTime }
}

val Database.classicProblems get() = this.sequenceOf(ClassicProblems)
