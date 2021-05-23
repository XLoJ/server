package cn.xlor.xloj.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.Instant

interface Submission : Entity<Submission> {
  companion object : Entity.Factory<Submission>() {
    const val WAITING = -3
    const val WRONG_ANSWER = -1
    const val ACCEPTED = 0
  }

  val id: Long

  val user: User

  val contest: Contest

  val body: String

  val language: String

  val verdict: Int

  val time: Int

  val memory: Int

  val pass: Int

  val createTime: Instant
}

object Submissions : Table<Submission>("submissions") {
  val id = long("id").primaryKey().bindTo { it.id }

  val user = long("user").references(Users) { it.user }

  val contest = long("contest").references(Contests) { it.contest }

  val body = text("body").bindTo { it.body }

  val language = text("language").bindTo { it.language }

  val verdict = int("verdict").bindTo { it.verdict }

  val time = int("time").bindTo { it.time }

  val memory = int("memory").bindTo { it.memory }

  val pass = int("pass").bindTo { it.pass }

  val createTime = timestamp("create_time").bindTo { it.createTime }
}

val Database.submissions get() = this.sequenceOf(Submissions)
