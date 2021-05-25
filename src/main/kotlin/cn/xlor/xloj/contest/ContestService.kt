package cn.xlor.xloj.contest

import cn.xlor.xloj.contest.dto.ContestWithWriter
import cn.xlor.xloj.contest.dto.DetailContest
import cn.xlor.xloj.contest.dto.UpdateContestDto
import cn.xlor.xloj.exception.NotFoundException
import cn.xlor.xloj.model.Contest
import cn.xlor.xloj.model.UserProfile
import cn.xlor.xloj.model.toUserProfile
import cn.xlor.xloj.repository.ContestRepository
import cn.xlor.xloj.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ContestService(
  private val contestRepository: ContestRepository,
  private val userRepository: UserRepository
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

  private fun canUserFindContest(contest: Contest, user: UserProfile): Boolean {
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
      problems = emptyList()
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

    if (!canVisitorFindContest(contest)) {
      if (!canUserFindContest(contest, user)) {
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
      problems = emptyList()
    )
  }
}
