package cn.xlor.xloj.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.Instant

interface Problem : Entity<Problem> {
  companion object : Entity.Factory<Problem>()

  var id: Long

  var creatorId: Long

  var timeLimit: Int

  var memoryLimit: Int

  var tags: String

  var problemType: String

  var title: String

  var legend: String

  var inputFormat: String

  var outputFormat: String

  var notes: String

  var examples: String

  val testcaseNum: Int

  var createTime: Instant

  var updateTime: Instant
}

object Problems : Table<Problem>("problems") {
  val id = long("id").primaryKey().bindTo { it.id }

  val creatorId = long("creator").bindTo { it.creatorId }

  val timeLimit = int("time_limit").bindTo { it.timeLimit }

  val memoryLimit = int("memory_limit").bindTo { it.memoryLimit }

  val tags = text("tags").bindTo { it.tags }

  val problemType = text("problem_type").bindTo { it.problemType }

  val title = text("title").bindTo { it.title }

  val legend = text("legend").bindTo { it.legend }

  val inputFormat = text("input_format").bindTo { it.inputFormat }

  val outputFormat = text("output_format").bindTo { it.outputFormat }

  val notes = text("notes").bindTo { it.notes }

  val examples = text("examples").bindTo { it.examples }

  val testcaseNum = int("testcase_num").bindTo { it.testcaseNum }

  val createTime = timestamp("create_time").bindTo { it.createTime }

  val updateTime = timestamp("update_time").bindTo { it.updateTime }
}

val Database.problems get() = this.sequenceOf(Problems)
