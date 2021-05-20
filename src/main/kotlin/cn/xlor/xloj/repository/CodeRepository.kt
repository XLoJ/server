package cn.xlor.xloj.repository

import cn.xlor.xloj.model.ClassicProblemCode
import cn.xlor.xloj.model.ClassicProblemCodes
import cn.xlor.xloj.model.classicProblemCodes
import cn.xlor.xloj.polygon.dto.UploadCodeDto
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.toList
import org.springframework.stereotype.Repository

@Repository
class CodeRepository(
  private val database: Database
) {
  fun findCodeById(cid: Long): ClassicProblemCode? {
    return database.classicProblemCodes.find { it.id eq cid }
  }

  fun findCodeByCPId(cpid: Long, cid: Long): ClassicProblemCode? {
    return database.classicProblemCodes.find { (it.id eq cid) and (it.parent eq cpid) }
  }

  fun findAllGenerators(cpid: Long): List<ClassicProblemCode> {
    return database.classicProblemCodes.filter { (it.parent eq cpid) and (it.type eq "generator") }
      .toList()
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

  fun removeCode(cid: Long) {
    database.delete(ClassicProblemCodes) {
      it.id eq cid
    }
  }
}
