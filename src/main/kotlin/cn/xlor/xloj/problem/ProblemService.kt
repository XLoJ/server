package cn.xlor.xloj.problem

import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.model.UserProfile
import cn.xlor.xloj.repository.ProblemRepository
import cn.xlor.xloj.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class ProblemService(
  private val userRepository: UserRepository,
  private val problemRepository: ProblemRepository
) {
  fun canUserAccessProblem(user: UserProfile, problem: Problem): Boolean {
    if (problem.creatorId == user.id) return true
    if (userRepository.isUserAdmin(user.id)) return true
    if (problemRepository.canUserAccessProblem(user.id, problem.id)) return true
    return false
  }
}
