package cn.xlor.xloj.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.text

interface ClassicProblemCode : Entity<ClassicProblemCode> {
  companion object : Entity.Factory<ClassicProblemCode>()

  val id: Long

  val parent: Long

  val type: String

  var name: String

  var body: String

  var language: String

  var description: String

  var version: Int
}

object ClassicProblemCodes :
  Table<ClassicProblemCode>("classic_problem__codes") {
  val id = long("id").primaryKey().bindTo { it.id }

  // Join to Classic problem ID
  val parent = long("parent").bindTo { it.parent }

  val type = text("type").bindTo { it.type }

  val name = text("name").bindTo { it.name }

  val language = text("language").bindTo { it.language }

  val body = text("body").bindTo { it.body }

  val description = text("description").bindTo { it.description }

  val version = int("version").bindTo { it.version }
}

val Database.classicProblemCodes get() = this.sequenceOf(ClassicProblemCodes)
