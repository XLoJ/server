package cn.xlor.xloj.polygon

import cn.xlor.xloj.model.ClassicProblem
import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.polygon.dto.DetailClassicProblem
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
  fun findUserProblemList(uid: Long): List<Problem> {
    return problemRepository.findUserProblemList(uid)
  }

  fun createClassicProblem(name: String, creatorId: Long): Problem {
    val newProblemId =
      problemRepository.createClassicProblem(name, creatorId)
    return problemRepository.findProblemById(newProblemId)!!
  }

  fun findDetailClassicProblem(parentId: Long): DetailClassicProblem {
    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(parentId)
    return DetailClassicProblem(
      classicProblem.id,
      classicProblem.parent,
      classicProblem.name,
      classicProblem.status,
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
