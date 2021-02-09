package cn.xlor.xloj.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.long

interface UserGroup : Entity<UserGroup> {
  companion object : Entity.Factory<UserGroup>()

  var id: Long

  var uid: Long

  var group: Group
}

object UserGroups : Table<UserGroup>("user__groups") {
  val id = long("id").primaryKey().bindTo { it.id }

  val uid = long("uid").bindTo { it.uid }

  val group = long("gid").references(Groups) { it.group }
}

val Database.userGroups get() = this.sequenceOf(UserGroups)
