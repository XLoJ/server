package cn.xlor.xloj.polygon

import cn.xlor.xloj.exception.BadRequestException
import cn.xlor.xloj.exception.NotFoundException
import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.model.Submission
import cn.xlor.xloj.model.UserProfile
import cn.xlor.xloj.problem.dto.ClassicSubmissionDto
import cn.xlor.xloj.problem.dto.DetailClassicSubmission
import cn.xlor.xloj.problem.judge.ClassicJudgeService
import cn.xlor.xloj.problem.listener.ClassicJudgeMessageService
import cn.xlor.xloj.repository.ContestRepository
import cn.xlor.xloj.repository.SubmissionRepository
import org.springframework.stereotype.Service

@Service
class TestJudgeService(
  private val classicJudgeService: ClassicJudgeService,
  private val classicJudgeMessageService: ClassicJudgeMessageService,
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

  fun findTestJudgeSubmissionDetail(
    problem: Problem,
    user: UserProfile,
    submissionId: Long
  ): DetailClassicSubmission {
    val submission = submissionRepository.findSubmissionById(submissionId)
      ?: throw NotFoundException("未找到提交 $submissionId")
    if (submission.user != user.id) {
      throw BadRequestException("提交 $submissionId 不是您发起的")
    }
    if (submission.problem != problem.id) {
      throw BadRequestException("提交 $submissionId 不属于当前题目")
    }
    if (submission.contest.id != contestRepository.polygonContest().id) {
      throw BadRequestException("提交 $submissionId 不属于 Polygon")
    }
    val messages =
      classicJudgeMessageService.findClassicJudgeMessage(submissionId)
    return DetailClassicSubmission(
      submission.id,
      user,
      submission.contest,
      problem.id,
      submission.body,
      submission.language,
      submission.verdict,
      submission.time,
      submission.memory,
      submission.pass,
      submission.createTime,
      messages
    )
  }

  fun runTestJudge(
    problem: Problem,
    user: UserProfile,
    classicSubmissionDto: ClassicSubmissionDto
  ): Submission {
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
    return submission
  }
}
