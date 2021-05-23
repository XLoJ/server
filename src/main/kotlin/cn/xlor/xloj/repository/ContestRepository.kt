package cn.xlor.xloj.repository

import cn.xlor.xloj.model.Contest
import cn.xlor.xloj.model.contests
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.find
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
}
