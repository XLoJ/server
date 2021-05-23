package cn.xlor.xloj.repository

import cn.xlor.xloj.model.ClassicJudge
import cn.xlor.xloj.model.ClassicJudges
import cn.xlor.xloj.model.classicJudges
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.dsl.update
import org.ktorm.entity.find
import org.springframework.stereotype.Repository

@Repository
class ClassicJudgeRepository(
  private val database: Database
) {
  fun findClassicJudge(pid: Long): ClassicJudge? {
    return database.classicJudges.find { it.parent eq pid }
  }

  fun setClassicJudge(
    pid: Long,
    problemName: String,
    version: Int,
    maxTime: Int,
    maxMemory: Int,
    checkerName: String,
    checkerLanguage: String,
    size: Int
  ) {
    if (findClassicJudge(pid) == null) {
      database.insert(ClassicJudges) {
        set(it.parent, pid)
        set(it.problemName, problemName)
        set(it.version, version)
        set(it.maxTime, maxTime)
        set(it.maxMemory, maxMemory)
        set(it.checkerName, checkerName)
        set(it.checkerLanguage, checkerLanguage)
        set(it.size, size)
      }
    } else {
      database.update(ClassicJudges) {
        set(it.version, version)
        set(it.problemName, problemName)
        set(it.checkerName, checkerName)
        set(it.maxTime, maxTime)
        set(it.maxMemory, maxMemory)
        set(it.checkerLanguage, checkerLanguage)
        set(it.size, size)
        where { it.parent eq pid }
      }
    }
  }
}
