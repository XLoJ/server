package cn.xlor.xloj.contest

import cn.xlor.xloj.contest.dto.ContestWithWriter
import cn.xlor.xloj.contest.dto.DetailContest
import cn.xlor.xloj.exception.NotFoundException
import cn.xlor.xloj.model.toUserProfile
import cn.xlor.xloj.repository.ContestRepository
import cn.xlor.xloj.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class ContestService(
  private val contestRepository: ContestRepository,
  private val userRepository: UserRepository
) {
  fun findAllPublicContests(): List<ContestWithWriter> {
    return contestRepository.findAllPublicContests().map {
      val creator = userRepository.findOneUserById(it.creator)!!.toUserProfile()
      val writers =
        contestRepository.findContestWritersById(it.id)
      ContestWithWriter(
        id = it.id,
        name = it.name,
        description = it.description,
        startTime = it.startTime,
        duration = it.duration,
        creator = creator,
        writers = listOf(creator) + writers
      )
    }
  }

  fun findDetailContest(contestId: Long): DetailContest {
    val contest = contestRepository.findContestById(contestId)
      ?: throw NotFoundException("未找到比赛 ${contestId}.")
    val creator = userRepository.findOneUserById(contest.creator)!!
      .toUserProfile()
    val writers = contestRepository.findContestWritersById(contestId)

    return DetailContest(
      id = contestId,
      name = contest.name,
      description = contest.description,
      startTime = contest.startTime,
      duration = contest.duration,
      creator = creator,
      writers = listOf(creator) + writers,
      problems = emptyList()
    )
  }
}
