package cn.xlor.xloj.contest

import cn.xlor.xloj.contest.dto.DetailContest
import cn.xlor.xloj.exception.NotFoundException
import cn.xlor.xloj.model.Contest
import cn.xlor.xloj.model.toUserProfile
import cn.xlor.xloj.repository.ContestRepository
import cn.xlor.xloj.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class ContestService(
  private val contestRepository: ContestRepository,
  private val userRepository: UserRepository
) {
  fun findAllPublicContests(): List<Contest> {
    return contestRepository.findAllPublicContests()
  }

  fun findDetailContest(contestId: Long): DetailContest {
    val contest = contestRepository.findContestById(contestId)
      ?: throw NotFoundException("未找到比赛 ${contestId}.")
    return DetailContest(
      id = contestId,
      name = contest.name,
      description = contest.description,
      startTime = contest.startTime,
      duration = contest.duration,
      creator = userRepository.findOneUserById(contest.creator)!!
        .toUserProfile(),
      writers = emptyList(),
      problems = emptyList()
    )
  }
}
