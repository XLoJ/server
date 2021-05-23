package cn.xlor.xloj.problem.judge

import cn.xlor.xloj.exception.NotFoundException
import cn.xlor.xloj.repository.ClassicJudgeRepository
import org.springframework.stereotype.Service

@Service
class ClassicJudgeService(
  private val classicJudgeRepository: ClassicJudgeRepository
) {
  fun runClassicJudge(pid: Long) {
    val classicJudge = classicJudgeRepository.findClassicJudge(pid)
      ?: throw NotFoundException("Classic judge ${pid} 信息未找到")

  }
}
