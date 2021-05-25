package cn.xlor.xloj.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.text

interface ContestUser : Entity<ContestUser> {
  companion object : Entity.Factory<ContestUser>() {
    const val WriterType = "writer"

    const val ManagerType = "manager"

    const val ParticipantType = "participant"
  }

  val id: Long

  val contest: Contest

  val user: Long

  val type: String
}

object ContestUsers : Table<ContestUser>("contest__users") {
  val id = long("id").primaryKey().bindTo { it.id }

  val contest = long("contest").references(Contests) { it.contest }

  val user = long("user").bindTo { it.user }

  val type = text("type").bindTo { it.type }
}

val Database.contestUsers get() = this.sequenceOf(ContestUsers)

