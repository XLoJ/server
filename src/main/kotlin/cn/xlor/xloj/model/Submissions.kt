package cn.xlor.xloj.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.Instant

interface Submission : Entity<Submission> {
  companion object : Entity.Factory<Submission>() {
    const val Running = -5
    const val Compiling = -4
    const val Waiting = -3
    const val Finished = -2
    const val WrongAnswer = -1
    const val Accepted = 0
    const val TimeLimitExceeded = 1
    const val IdlenessLimitExceeded = 2
    const val MemoryLimitExceeded = 3
    const val RuntimeError = 4
    const val SystemError = 5
    const val CompileError = 6
    const val JudgeError = 8
    const val TestCaseError = 9
  }

  val id: Long

  val user: Long

  val contest: Contest

  val problem: Long

  val body: String

  val language: String

  val verdict: Int

  val time: Int

  val memory: Double

  val pass: Int

  val from: String

  val createTime: Instant
}

object Submissions : Table<Submission>("submissions") {
  val id = long("id").primaryKey().bindTo { it.id }

  val user = long("user").bindTo { it.user }

  val contest = long("contest").references(Contests) { it.contest }

  val problem = long("problem").bindTo { it.problem }

  val body = text("body").bindTo { it.body }

  val language = text("language").bindTo { it.language }

  val verdict = int("verdict").bindTo { it.verdict }

  val time = int("time").bindTo { it.time }

  val memory = double("memory").bindTo { it.memory }

  val pass = int("pass").bindTo { it.pass }

  val from = text("from").bindTo { it.from }

  val createTime = timestamp("create_time").bindTo { it.createTime }
}

val Database.submissions get() = this.sequenceOf(Submissions)
