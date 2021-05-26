package cn.xlor.xloj.contest

import cn.xlor.xloj.contest.dto.ContestWithWriter
import cn.xlor.xloj.contest.dto.DetailContest
import cn.xlor.xloj.contest.dto.SubmissionSummary
import cn.xlor.xloj.contest.dto.UpdateContestDto
import cn.xlor.xloj.exception.BadRequestException
import cn.xlor.xloj.exception.NotFoundException
import cn.xlor.xloj.exception.UnAuthorizeException
import cn.xlor.xloj.model.*
import cn.xlor.xloj.problem.ClassicJudgeService
import cn.xlor.xloj.problem.ProblemService
import cn.xlor.xloj.problem.dto.ClassicSubmissionDto
import cn.xlor.xloj.problem.dto.DetailClassicSubmission
import cn.xlor.xloj.problem.listener.ClassicJudgeMessageService
import cn.xlor.xloj.repository.ContestRepository
import cn.xlor.xloj.repository.ProblemRepository
import cn.xlor.xloj.repository.SubmissionRepository
import cn.xlor.xloj.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ContestService(
  private val contestRepository: ContestRepository,
  private val userRepository: UserRepository,
  private val problemRepository: ProblemRepository,
  private val problemService: ProblemService,
  private val submissionRepository: SubmissionRepository,
  private val classicJudgeService: ClassicJudgeService,
  private val classisJudgeMessageService: ClassicJudgeMessageService
) {
  fun createContest(name: String, user: UserProfile): DetailContest {
    val contestId = contestRepository.createContest(name, user.id)
    return findDetailContestWithUser(contestId, user)
  }

  fun updateContest(
    contest: Contest,
    updateContestDto: UpdateContestDto
  ): Contest {
    contest.name = updateContestDto.name ?: contest.name
    contest.description = updateContestDto.description ?: contest.description
    contest.startTime = Instant.parse(updateContestDto.startTime)
    contest.duration = updateContestDto.duration ?: contest.duration
    contestRepository.updateContest(contest.id, contest)
    return contestRepository.findContestById(contest.id)!!
  }

  fun updateContestPublic(
    contest: Contest,
    public: Boolean
  ): Contest {
    contestRepository.updateContestPublic(contest.id, public)
    return contestRepository.findContestById(contest.id)!!
  }

  private fun addWritersToContest(contest: Contest): ContestWithWriter {
    val creator =
      userRepository.findOneUserById(contest.creator)!!.toUserProfile()
    val writers =
      contestRepository.findContestWritersById(contest.id)
    return ContestWithWriter(
      id = contest.id,
      name = contest.name,
      description = contest.description,
      startTime = contest.startTime,
      duration = contest.duration,
      creator = creator,
      writers = listOf(creator) + writers,
      public = contest.public
    )
  }

  fun findAllPublicContests(): List<ContestWithWriter> {
    return contestRepository.findAllPublicContests().map {
      addWritersToContest(it)
    }
  }

  fun findAllUserContests(user: UserProfile): List<ContestWithWriter> {
    return if (userRepository.isUserAdmin(user.id)) {
      contestRepository.findAllContests().map { addWritersToContest(it) }
    } else {
      (
          findAllPublicContests()
              + contestRepository.findAllUserCreateContests(user.id)
            .map { addWritersToContest(it) }
              + contestRepository.findAllUserManageContests(user.id)
          ).sortedByDescending { it.id }
    }
  }

  /**
   * Visitor contest access condition:
   * 1. Public contest
   * 2. After contest begin
   */
  private fun canVisitorFindContest(contest: Contest): Boolean {
    return !(!contest.public || contest.startTime.toEpochMilli() > Instant.now()
      .toEpochMilli())
  }

  /**
   * User contest access condition:
   * 1. admin
   * 2. creator
   * 3. writer or manager
   */
  private fun canUserFindFullContest(
    contest: Contest,
    user: UserProfile
  ): Boolean {
    return userRepository.isUserAdmin(user.id) || contest.creator == user.id || contestRepository.checkUserManageContest(
      contest.id,
      user.id
    )
  }

  /**
   * 公开比赛结束后才能查看提交内容
   */
  private fun canVisitorFindContestSubmission(contest: Contest): Boolean {
    if (!contest.public) return false
    return contest.startTime.toEpochMilli() + contest.duration * 60 * 1000 > Instant.now()
      .toEpochMilli()
  }

  fun findPublicDetailContest(contestId: Long): DetailContest {
    val contest = contestRepository.findContestById(contestId)
      ?: throw NotFoundException("无权访问比赛 $contestId.")
    val creator = userRepository.findOneUserById(contest.creator)!!
      .toUserProfile()
    val writers = contestRepository.findContestWritersById(contestId)

    if (!canVisitorFindContest(contest)) {
      throw NotFoundException("无权访问比赛 ${contest.id}.")
    }

    return DetailContest(
      id = contestId,
      name = contest.name,
      description = contest.description,
      startTime = contest.startTime,
      duration = contest.duration,
      public = contest.public,
      creator = creator,
      writers = listOf(creator) + writers,
      problems = contestRepository.findVisibleContestProblems(contestId)
    )
  }

  fun findDetailContestWithUser(
    contestId: Long,
    user: UserProfile
  ): DetailContest {
    val contest = contestRepository.findContestById(contestId)
      ?: throw NotFoundException("未找到比赛 $contestId.")
    val creator = userRepository.findOneUserById(contest.creator)!!
      .toUserProfile()
    val writers = contestRepository.findContestWritersById(contestId)

    val canVisitorFindPublic = canVisitorFindContest(contest)
    val canUserFindFull = canUserFindFullContest(contest, user)
    if (!canVisitorFindPublic) {
      if (!canUserFindFull) {
        throw NotFoundException("无权访问比赛 ${contest.id}.")
      }
    }

    return DetailContest(
      id = contestId,
      name = contest.name,
      description = contest.description,
      startTime = contest.startTime,
      duration = contest.duration,
      public = contest.public,
      creator = creator,
      writers = listOf(creator) + writers,
      problems = if (canUserFindFull) {
        contestRepository.findAllContestProblems(contestId)
      } else {
        contestRepository.findVisibleContestProblems(contestId)
      }
    )
  }

  fun findContestProblemByIndex(
    user: UserProfile?,
    contestId: Long,
    problemIndex: Int
  ): ContestProblem {
    val contest = contestRepository.findContestById(contestId)
      ?: throw NotFoundException("无权访问比赛 $contestId.")
    val contestProblem =
      contestRepository.findContestProblemByContestAndIndex(
        contestId,
        problemIndex
      )
        ?: throw NotFoundException("未找到比赛题目")
    if (contestProblem.contest.id != contest.id) {
      throw BadRequestException("比赛题目不属于比赛 ${contest.id}.")
    }

    // 比赛管理员
    val isManager = user != null && canUserFindFullContest(contest, user)
    return if (isManager) {
      contestProblem
    } else {
      if (contestProblem.visible && canVisitorFindContest(contest)) {
        contestProblem
      } else {
        throw NotFoundException("未找到比赛题目")
      }
    }
  }


  fun findContestProblemByCpid(
    user: UserProfile?,
    contestId: Long,
    pid: Long
  ): ContestProblem {
    val contest = contestRepository.findContestById(contestId)
      ?: throw NotFoundException("无权访问比赛 $contestId.")
    val contestProblem =
      contestRepository.findContestProblemByContestAndCpid(
        contestId,
        pid
      )
        ?: throw NotFoundException("未找到比赛题目")
    if (contestProblem.contest.id != contest.id) {
      throw BadRequestException("比赛题目不属于比赛 ${contest.id}.")
    }

    // 比赛管理员
    val isManager = user != null && canUserFindFullContest(contest, user)
    return if (isManager) {
      contestProblem
    } else {
      if (contestProblem.visible && canVisitorFindContest(contest)) {
        contestProblem
      } else {
        throw NotFoundException("未找到比赛题目")
      }
    }
  }

  // -- Submission
  fun submitCode(
    user: UserProfile,
    contestId: Long,
    cpId: Long,
    submission: ClassicSubmissionDto
  ): Submission {
    val contest = contestRepository.findContestById(contestId)
      ?: throw NotFoundException("未找到比赛 $contestId.")
    val contestProblem = contestRepository.findContestProblemById(cpId)
      ?: throw NotFoundException("未找到比赛题目 $cpId.")
    if (contestProblem.contest.id != contest.id) {
      throw BadRequestException("比赛题目 $cpId. 不属于比赛 ${contest.id}.")
    }

    val newSubmissionId = submissionRepository.createSubmission(
      user.id,
      contest.id,
      contestProblem.problem.id,
      submission.body,
      submission.language
    )
    val newSubmission =
      submissionRepository.findSubmissionById(newSubmissionId)!!
    classicJudgeService.runClassicJudge(newSubmission)
    return newSubmission
  }

  fun findUserSubmissionDetail(
    user: UserProfile?,
    contestId: Long,
    submissionId: Long
  ): DetailClassicSubmission {
    val submission = submissionRepository.findSubmissionById(submissionId)
      ?: throw NotFoundException("未找到提交 $submissionId")
    val contest = contestRepository.findContestById(contestId)
      ?: throw NotFoundException("未找到比赛 $contestId.")
    if (submission.contest.id != contestId) {
      throw BadRequestException("提交 $submissionId 不属于比赛 ${contestId}.")
    }

    // 比赛管理员
    val isManager = user != null && canUserFindFullContest(contest, user)
    // 用户自己的提交
    val isSelfSubmission = user != null && submission.user == user.id
    // 游客
    val isVisitor = canVisitorFindContest(contest)

    if (isManager || isSelfSubmission || isVisitor) {
      val messages =
        classisJudgeMessageService.findClassicJudgeMessage(submissionId)
      return DetailClassicSubmission(
        submission.id,
        userRepository.findOneUserById(submission.user)!!.toUserProfile(),
        submission.contest,
        submission.problem,
        submission.body,
        submission.language,
        submission.verdict,
        submission.time,
        submission.memory,
        submission.pass,
        submission.from,
        submission.createTime,
        messages
      )
    } else {
      throw UnAuthorizeException("无法访问提交 $submissionId.")
    }
  }

  private fun toSubmissionSummary(submission: Submission): SubmissionSummary {
    return SubmissionSummary(
      id = submission.id,
      user = userRepository.findOneUserById(submission.user)!!.toUserProfile(),
      contest = submission.contest,
      problem = problemRepository.findProblemById(submission.problem)!!,
      language = submission.language,
      verdict = submission.verdict,
      time = submission.time,
      memory = submission.memory,
      pass = submission.pass,
      from = submission.from,
      createTime = submission.createTime
    )
  }

  fun findUserSubmissions(
    user: UserProfile,
    contestId: Long
  ): List<SubmissionSummary> {
    contestRepository.findContestById(contestId)
      ?: throw NotFoundException("未找到比赛 $contestId.")

    return submissionRepository.findAllPolygonSubmissionsByContestAndUser(
      contestId,
      user.id
    ).map { toSubmissionSummary(it) }
  }

  fun findAllContestSubmissions(
    user: UserProfile?,
    contestId: Long
  ): List<SubmissionSummary> {
    contestRepository.findContestById(contestId)
      ?: throw NotFoundException("未找到比赛 $contestId.")
    return submissionRepository.findAllSubmissionsByContest(contestId)
      .map { toSubmissionSummary(it) }
  }

  // --- Contest Problems ---
  fun findPublicProblems(contestId: Long): List<ContestProblem> {
    return contestRepository.findVisibleContestProblems(contestId)
  }

  fun findUserProblems(
    contestId: Long,
    user: UserProfile
  ): List<ContestProblem> {
    if (userRepository.isUserAdmin(user.id)) {
      return contestRepository.findAllContestProblems(contestId)
    } else {
      return contestRepository.findVisibleContestProblems(contestId)
    }
  }

  fun pushContestProblem(
    user: UserProfile,
    contest: Contest,
    problemId: Long
  ): ContestProblem {
    val problem = problemRepository.findProblemById(problemId)
      ?: throw NotFoundException("未找到题目 $problemId.")
    if (!problemService.canUserAccessProblem(user, problem)) {
      throw UnAuthorizeException("用户 ${user.nickname} 无权访问题目 $problemId.")
    }
    return contestRepository.pushContestProblem(contest.id, problemId)
  }

  fun setContestProblemVisible(
    contest: Contest,
    cpId: Long,
    visible: Boolean
  ): ContestProblem {
    val contestProblem = contestRepository.findContestProblemById(cpId)
      ?: throw NotFoundException("未找到比赛题目 $cpId.")
    if (contestProblem.contest.id != contest.id) {
      throw BadRequestException("比赛题目 $cpId. 不属于比赛 ${contest.id}.")
    }
    contestRepository.setContestProblemVisible(cpId, visible)
    contestProblem.visible = visible
    return contestProblem
  }

  fun removeContestProblem(
    contest: Contest,
    cpId: Long
  ) {
    val contestProblem = contestRepository.findContestProblemById(cpId)
      ?: throw NotFoundException("未找到比赛题目 $cpId.")
    if (contestProblem.contest.id != contest.id) {
      throw BadRequestException("比赛题目 $cpId. 不属于比赛 ${contest.id}.")
    }
    return contestRepository.removeContestProblem(cpId)
  }
}
