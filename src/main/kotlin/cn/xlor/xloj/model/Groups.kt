package cn.xlor.xloj.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.text

interface Group : Entity<Group> {
  companion object : Entity.Factory<Group>()

  var id: Long

  var name: String
}

object Groups : Table<Group>("groups") {
  val id = long("id").primaryKey().bindTo { it.id }

  val name = text("name").bindTo { it.name }
}

val Database.groups get() = this.sequenceOf(Groups)
