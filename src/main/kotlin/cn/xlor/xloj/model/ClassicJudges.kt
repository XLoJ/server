package cn.xlor.xloj.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.text

interface ClassicJudge : Entity<ClassicJudge> {
  companion object : Entity.Factory<ClassicJudge>()

  val id: Long

  val parent: Long

  val version: Int

  val checkerName: String

  val checkerLanguage: String

  val size: Int
}

object ClassicJudges : Table<ClassicJudge>("classic_judges") {
  val id = long("id").primaryKey().bindTo { it.id }

  val parent = long("parent").bindTo { it.parent }

  val version = int("version").bindTo { it.version }

  val checkerName = text("checker_name").bindTo { it.checkerName }

  val checkerLanguage = text("checker_language").bindTo { it.checkerLanguage }

  val size = int("size").bindTo { it.size }
}

val Database.classicJudges get() = this.sequenceOf(ClassicJudges)
