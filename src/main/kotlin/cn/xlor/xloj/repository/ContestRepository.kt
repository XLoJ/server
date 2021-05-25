package cn.xlor.xloj.repository

import cn.xlor.xloj.model.Contest
import cn.xlor.xloj.model.contests
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.notEq
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.sortedBy
import org.ktorm.entity.toList
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository

@Repository
class ContestRepository(
  private val database: Database
) {
  @Cacheable(cacheNames = ["polygonContest"])
  fun polygonContest(): Contest {
    return database.contests.find { it.type eq Contest.PolygonType }!!
  }

  @Cacheable(cacheNames = ["LocalContest"])
  fun localContest(): Contest {
    return database.contests.find { it.type eq Contest.LocalType }!!
  }

  fun findAllPublicContests(): List<Contest> {
    return database.contests.filter { (it.public eq true) }
      .filter { (it.type notEq Contest.PolygonType) }
      .filter { (it.type notEq Contest.LocalType) }
      .filter { (it.type notEq Contest.RemoteType) }
      .sortedBy { it.id }.toList()
  }

  fun findContestById(contestId: Long): Contest? {
    return database.contests.find { it.id eq contestId }
  }
}
