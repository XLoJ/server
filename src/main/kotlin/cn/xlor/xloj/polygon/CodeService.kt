package cn.xlor.xloj.polygon

import cn.xlor.xloj.exception.NotFoundException
import cn.xlor.xloj.model.ClassicProblem
import cn.xlor.xloj.model.ClassicProblemCode
import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.polygon.dto.UploadCodeDto
import cn.xlor.xloj.repository.ClassicProblemRepository
import cn.xlor.xloj.repository.CodeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CodeService(
  private val codeRepository: CodeRepository,
  private val classicProblemRepository: ClassicProblemRepository,
  private val minIOService: MinIOService
) {
  @Transactional
  fun uploadCheckerToDatabase(
    problem: ClassicProblem,
    uploadCodeDto: UploadCodeDto
  ): Long {
    val cid =
      codeRepository.uploadCode(problem.id, "checker", uploadCodeDto)
    codeRepository.setProblemChecker(problem.id, cid)
    return cid
  }

  fun uploadChecker(
    problem: Problem,
    uploadCodeDto: UploadCodeDto
  ): ClassicProblemCode {
    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(problem.id)
    val checkerId = classicProblem.checker
    val checker = if (checkerId == null) {
      val newCheckerId =
        uploadCheckerToDatabase(classicProblem, uploadCodeDto)
      codeRepository.findCodeById(newCheckerId)!!
    } else {
      val checker = codeRepository.findCodeById(checkerId)!!
      codeRepository.updateCode(checkerId, uploadCodeDto, checker.version + 1)
      codeRepository.findCodeById(checkerId)!!
    }
    minIOService.uploadCodeToMinIO(classicProblem, checker)
    return checker
  }

  fun findChecker(problem: Problem): ClassicProblemCode {
    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(problem.id)
    val checkerId = classicProblem.checker
    if (checkerId != null) {
      return codeRepository.findCodeById(checkerId)!!
    } else {
      throw NotFoundException("题目 \"${classicProblem.id}-${classicProblem.name}\" 尚未设置 checker")
    }
  }

  @Transactional
  fun uploadValidatorToDatabase(
    problem: ClassicProblem,
    uploadCodeDto: UploadCodeDto
  ): Long {
    val cid =
      codeRepository.uploadCode(problem.id, "validator", uploadCodeDto)
    codeRepository.setProblemValidator(problem.id, cid)
    return cid
  }

  fun uploadValidator(
    problem: Problem,
    uploadCodeDto: UploadCodeDto
  ): ClassicProblemCode {
    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(problem.id)
    val validatorId = classicProblem.validator
    val validator = if (validatorId == null) {
      val newValidatorId =
        uploadValidatorToDatabase(classicProblem, uploadCodeDto)
      codeRepository.findCodeById(newValidatorId)!!
    } else {
      val validator = codeRepository.findCodeById(validatorId)!!
      codeRepository.updateCode(
        validatorId,
        uploadCodeDto,
        validator.version + 1
      )
      codeRepository.findCodeById(validatorId)!!
    }
    minIOService.uploadCodeToMinIO(classicProblem, validator)
    return validator
  }

  fun findValidator(problem: Problem): ClassicProblemCode {
    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(problem.id)
    val validatorId = classicProblem.validator
    if (validatorId != null) {
      return codeRepository.findCodeById(validatorId)!!
    } else {
      throw NotFoundException("题目 \"${classicProblem.id}-${classicProblem.name}\" 尚未设置 validator")
    }
  }

  @Transactional
  fun uploadSolutionToDatabase(
    problem: ClassicProblem,
    uploadCodeDto: UploadCodeDto
  ): Long {
    val cid = codeRepository.uploadCode(problem.id, "solution", uploadCodeDto)
    codeRepository.setProblemSolution(problem.id, cid)
    return cid
  }

  fun uploadSolution(
    problem: Problem,
    uploadCodeDto: UploadCodeDto
  ): ClassicProblemCode {
    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(problem.id)
    val solutionId = classicProblem.solution
    val solution = if (solutionId == null) {
      val newSolutionId =
        uploadSolutionToDatabase(classicProblem, uploadCodeDto)
      codeRepository.findCodeById(newSolutionId)!!
    } else {
      val solution = codeRepository.findCodeById(solutionId)!!
      codeRepository.updateCode(solutionId, uploadCodeDto, solution.version + 1)
      codeRepository.findCodeById(solutionId)!!
    }
    minIOService.uploadCodeToMinIO(classicProblem, solution)
    return solution
  }

  fun findSolution(problem: Problem): ClassicProblemCode {
    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(problem.id)
    val solutionId = classicProblem.solution
    if (solutionId != null) {
      return codeRepository.findCodeById(solutionId)!!
    } else {
      throw NotFoundException("题目 \"${classicProblem.id}-${classicProblem.name}\" 尚未设置正确 solution")
    }
  }
}
