package cn.xlor.xloj.polygon

import cn.xlor.xloj.model.ClassicProblem
import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.polygon.dto.DetailClassicProblem
import cn.xlor.xloj.polygon.dto.ProblemListItem
import cn.xlor.xloj.polygon.dto.UpdateProblemDto
import cn.xlor.xloj.repository.ClassicProblemRepository
import cn.xlor.xloj.repository.CodeRepository
import cn.xlor.xloj.repository.ProblemRepository
import org.springframework.stereotype.Service

@Service
class PolygonService(
  private val codeRepository: CodeRepository,
  private val problemRepository: ProblemRepository,
  private val classicProblemRepository: ClassicProblemRepository
) {
  fun findUserProblemList(uid: Long): List<ProblemListItem> {
    return problemRepository.findUserProblemList(uid).map { it ->
      val classicProblem =
        classicProblemRepository.findClassicProblemByParentId(it.id)
      if (classicProblem != null) {
        ProblemListItem(it.id, classicProblem.name, it.creatorId)
      } else {
        null
      }
    }.filterNotNull()
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
}
