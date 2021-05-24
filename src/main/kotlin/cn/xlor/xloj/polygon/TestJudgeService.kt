package cn.xlor.xloj.polygon

import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.model.Submission
import cn.xlor.xloj.model.UserProfile
import cn.xlor.xloj.problem.dto.ClassicSubmissionDto
import cn.xlor.xloj.problem.judge.ClassicJudgeService
import cn.xlor.xloj.repository.ContestRepository
import cn.xlor.xloj.repository.SubmissionRepository
import org.springframework.stereotype.Service

@Service
class TestJudgeService(
  private val classicJudgeService: ClassicJudgeService,
  private val contestRepository: ContestRepository,
  private val submissionRepository: SubmissionRepository
) {
  fun findUserAllSubmission(
    problem: Problem,
    user: UserProfile
  ): List<Submission> {
    return submissionRepository.findAllSubmissionsByProblemAndUser(
      problem.id,
      user.id
    )
  }

  /**
   * @return { "submissionId": ... }
   */
  fun runTestJudge(
    problem: Problem,
    user: UserProfile,
    classicSubmissionDto: ClassicSubmissionDto
  ): Map<String, Long> {
    val submissionId = submissionRepository.createSubmission(
      user.id,
      contestRepository.polygonContest().id,
      problem.id,
      classicSubmissionDto.body,
      classicSubmissionDto.language
    )
    val submission =
      submissionRepository.findSubmissionById(submissionId)!!
    classicJudgeService.runClassicJudge(submission)
    return mapOf("submissionId" to submissionId)
  }
}
