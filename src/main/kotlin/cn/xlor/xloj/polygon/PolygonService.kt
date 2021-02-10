package cn.xlor.xloj.polygon

import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.repository.ProblemRepository
import org.springframework.stereotype.Service

@Service
class PolygonService(
  private val problemRepository: ProblemRepository
) {
  fun findUserProblemList(uid: Long): List<Problem> {
    return problemRepository.findUserProblemList(uid)
  }
}
