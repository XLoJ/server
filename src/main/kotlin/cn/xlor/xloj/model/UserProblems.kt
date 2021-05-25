package cn.xlor.xloj.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.text

interface UserProblem : Entity<UserProblem> {
  companion object : Entity.Factory<UserProblem>() {
    const val LevelRead = "read"
  }

  var id: Long

  var uid: Long

  var problem: Problem

  var level: String
}

object UserProblems : Table<UserProblem>("user__problems") {
  val id = long("id").primaryKey().bindTo { it.id }

  val uid = long("uid").bindTo { it.uid }

  val problem = long("pid").references(Problems) { it.problem }

  val level = text("level").bindTo { it.level }
}

val Database.userProblems get() = this.sequenceOf(UserProblems)
