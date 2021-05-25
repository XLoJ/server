package cn.xlor.xloj.contest

import cn.xlor.xloj.contest.dto.ContestWithWriter
import cn.xlor.xloj.contest.dto.DetailContest
import cn.xlor.xloj.contest.dto.UpdateContestDto
import cn.xlor.xloj.exception.BadRequestException
import cn.xlor.xloj.exception.NotFoundException
import cn.xlor.xloj.exception.UnAuthorizeException
import cn.xlor.xloj.model.Contest
import cn.xlor.xloj.model.ContestProblem
import cn.xlor.xloj.model.UserProfile
import cn.xlor.xloj.model.toUserProfile
import cn.xlor.xloj.problem.ProblemService
import cn.xlor.xloj.repository.ContestRepository
import cn.xlor.xloj.repository.ProblemRepository
import cn.xlor.xloj.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ContestService(
  private val contestRepository: ContestRepository,
  private val userRepository: UserRepository,
  private val problemRepository: ProblemRepository,
  private val problemService: ProblemService
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
