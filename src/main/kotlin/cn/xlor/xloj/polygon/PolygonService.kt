package cn.xlor.xloj.polygon

import cn.xlor.xloj.PolygonQueuName
import cn.xlor.xloj.model.ClassicProblem
import cn.xlor.xloj.model.ClassicProblemCode
import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.polygon.dto.DetailClassicProblem
import cn.xlor.xloj.polygon.dto.ProblemListItem
import cn.xlor.xloj.polygon.dto.UpdateProblemDto
import cn.xlor.xloj.polygon.listener.PolygonMessageService
import cn.xlor.xloj.repository.ClassicProblemRepository
import cn.xlor.xloj.repository.CodeRepository
import cn.xlor.xloj.repository.ProblemRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class PolygonService(
  private val rabbitTemplate: RabbitTemplate,
  private val codeRepository: CodeRepository,
  private val classicProblemRepository: ClassicProblemRepository,
  private val problemRepository: ProblemRepository,
  private val codeService: CodeService,
  private val staticFileService: StaticFileService,
  private val minIOService: MinIOService,
  private val polygonMessageService: PolygonMessageService
) {
  fun findUserProblemList(uid: Long): List<ProblemListItem> {
    return problemRepository.findUserProblemList(uid).mapNotNull {
      val classicProblem =
        classicProblemRepository.findClassicProblemByParentId(it.id)
      if (classicProblem != null) {
        ProblemListItem(it.id, classicProblem.name, it.creatorId)
      } else {
        null
      }
    }
  }

  fun createClassicProblem(name: String, creatorId: Long): Problem {
    val newProblemId =
      problemRepository.createClassicProblem(name, creatorId)
    return problemRepository.findProblemById(newProblemId)!!
  }

  fun findDetailClassicProblem(problem: Problem): DetailClassicProblem {
    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(problem.id)
    return DetailClassicProblem(
      classicProblem.id,
      classicProblem.parent,
      classicProblem.status,
      classicProblem.name,
      problem.timeLimit,
      problem.memoryLimit,
      problem.tags,
      if (classicProblem.checker != null) {
        codeRepository.findCodeByCPId(
          classicProblem.id,
          classicProblem.checker!!
        )
      } else {
        null
      },
      if (classicProblem.validator != null) {
        codeRepository.findCodeByCPId(
          classicProblem.id,
          classicProblem.validator!!
        )
      } else {
        null
      },
      if (classicProblem.solution != null) {
        codeRepository.findCodeByCPId(
          classicProblem.id,
          classicProblem.solution!!
        )
      } else {
        null
      },
      codeRepository.findAllGenerators(classicProblem.id),
      classicProblem.testcases,
      classicProblem.version,
      classicProblem.createTime,
      classicProblem.updateTime
    )
  }

  fun updateProblemInfo(
    problem: Problem,
    updateProblemDto: UpdateProblemDto
  ): Problem {
    problem.timeLimit = updateProblemDto.timeLimit ?: problem.timeLimit
    problem.memoryLimit = updateProblemDto.memoryLimit ?: problem.memoryLimit
    problem.tags = updateProblemDto.tags ?: problem.tags
    problem.title = updateProblemDto.title ?: problem.title
    problem.legend = updateProblemDto.legend ?: problem.legend
    problem.inputFormat = updateProblemDto.inputFormat ?: problem.inputFormat
    problem.outputFormat = updateProblemDto.outputFormat ?: problem.outputFormat
    problem.notes = updateProblemDto.notes ?: problem.notes
    problemRepository.updateProblemInfo(problem)
    return problem
  }

  fun updateClassicProblemTestcases(
    problem: Problem,
    testcases: String
  ): ClassicProblem {
    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(problem.id)
    classicProblem.testcases = testcases
    classicProblemRepository.updateClassicProblemTestcases(
      classicProblem.id,
      testcases
    )
    return classicProblem
  }

  fun buildClassicProblem(problem: Problem): ClassicProblem {
    val payload = mutableMapOf<String, Any>()

    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(problem.id)

    val basename = "${problem.id}-${classicProblem.name}"
    payload += "problem" to basename

    payload += "version" to classicProblem.version
    payload += "timeLimit" to problem.timeLimit
    payload += "memoryLimit" to problem.memoryLimit
    payload += "testcases" to classicProblem.testcases

    payload += "staticFiles" to staticFileService.getAllStaticFilename(problem)
      .map {
        mapOf("name" to it, "fullname" to "static/$it")
      }

    fun transCode(code: ClassicProblemCode) = mapOf<String, Any>(
      "id" to code.id,
      "name" to code.name,
      "language" to code.language,
      "type" to code.type,
      "version" to code.version,
      "fullname" to minIOService.codeFilename(problem.id, classicProblem, code)
        .split("/").last()
    )
    payload += "checker" to transCode(codeService.findChecker(problem))
    payload += "validator" to transCode(codeService.findValidator(problem))
    payload += "solution" to transCode(codeService.findSolution(problem))
    payload += "generators" to codeRepository.findAllGenerators(classicProblem.id)
      .map(::transCode)

    polygonMessageService.resetPolygonMessage(basename, classicProblem.version)

    rabbitTemplate.convertAndSend(PolygonQueuName, payload)

    return classicProblem
  }

  fun findClassicProblemBuildMessage(
    problem: Problem,
    version: Int
  ): List<Any> {
    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(problem.id)

    val basename = "${problem.id}-${classicProblem.name}"

    return polygonMessageService.findPolygonMessage(basename, version)
  }

  fun findAllClassicProblemBuildMessage(problem: Problem): Map<Int, List<Any>> {
    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(problem.id)

    val basename = "${problem.id}-${classicProblem.name}"

    val map = HashMap<Int, List<Any>>()
    for (version in 0..classicProblem.version) {
      map += version to polygonMessageService.findPolygonMessage(
        basename,
        version
      )
    }

    return map
  }
}
