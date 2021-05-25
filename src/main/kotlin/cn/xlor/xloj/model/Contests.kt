package cn.xlor.xloj.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.Instant

interface Contest : Entity<Contest> {
  companion object : Entity.Factory<Contest>() {
    const val PolygonType = "Polygon"

    const val LocalType = "Local"

    const val RemoteType = "Remote"
  }

  val id: Long

  val creator: Long

  val name: String

  val description: String

  val startTime: Instant

  val duration: Int

  val type: String

  val public: Boolean
}

object Contests : Table<Contest>("contests") {
  val id = long("id").primaryKey().bindTo { it.id }

  val creator = long("creator").bindTo { it.creator }

  val name = text("name").bindTo { it.name }

  val description = text("description").bindTo { it.description }

  val startTime = timestamp("start_time").bindTo { it.startTime }

  val duration = int("duration").bindTo { it.duration }

  val type = text("type").bindTo { it.type }

  val public = boolean("public").bindTo { it.public }
}

val Database.contests get() = this.sequenceOf(Contests)
