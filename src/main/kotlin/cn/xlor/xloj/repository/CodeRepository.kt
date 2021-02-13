package cn.xlor.xloj.repository

import cn.xlor.xloj.model.ClassicProblemCode
import cn.xlor.xloj.model.ClassicProblemCodes
import cn.xlor.xloj.model.ClassicProblems
import cn.xlor.xloj.model.classicProblemCodes
import cn.xlor.xloj.polygon.dto.UploadCodeDto
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.dsl.update
import org.ktorm.entity.find
import org.springframework.stereotype.Repository

@Repository
class CodeRepository(
  private val database: Database
) {
  fun findCodeById(cid: Long): ClassicProblemCode? {
    return database.classicProblemCodes.find { it.id eq cid }
  }

  fun uploadCode(cpid: Long, type: String, uploadCodeDto: UploadCodeDto): Long {
    return database.insertAndGenerateKey(ClassicProblemCodes) {
      set(it.parent, cpid)
      set(it.type, type)
      set(it.name, uploadCodeDto.name)
      set(it.body, uploadCodeDto.body)
      set(it.language, uploadCodeDto.language)
      set(it.description, uploadCodeDto.description)
    } as Long
  }

  fun updateCode(cid: Long, uploadCodeDto: UploadCodeDto, version: Int) {
    database.update(ClassicProblemCodes) {
      set(it.name, uploadCodeDto.name)
      set(it.body, uploadCodeDto.body)
      set(it.language, uploadCodeDto.language)
      set(it.description, uploadCodeDto.description)
      set(it.version, version)
      where { it.id eq cid }
    }
  }

  fun setProblemChecker(cpid: Long, cid: Long) {
    database.update(ClassicProblems) {
      set(it.checker, cid)
      where { it.id eq cpid }
    }
  }

  fun setProblemValidator(cpid: Long, cid: Long) {
    database.update(ClassicProblems) {
      set(it.validator, cid)
      where { it.id eq cpid }
    }
  }

  fun setProblemSolution(cpid: Long, cid: Long) {
    database.update(ClassicProblems) {
      set(it.solution, cid)
      where { it.id eq cpid }
    }
  }
}
