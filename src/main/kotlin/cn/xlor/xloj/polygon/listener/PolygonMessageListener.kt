package cn.xlor.xloj.polygon.listener

import cn.xlor.xloj.PolygonMessageQueueName
import cn.xlor.xloj.repository.ClassicJudgeRepository
import cn.xlor.xloj.repository.ProblemRepository
import cn.xlor.xloj.security.ProblemLockService
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class PolygonMessageListener(
  private val problemRepository: ProblemRepository,
  private val polygonMessageService: PolygonMessageService,
  private val problemLockService: ProblemLockService,
  private val classicJudgeRepository: ClassicJudgeRepository
) {
  @RabbitListener(queuesToDeclare = [Queue(PolygonMessageQueueName)])
  fun handlePolygonMessage(message: PolygonMessage) {
    val pid = message.problem.split('-').first().toLong()
    if (message.action == PolygonMessage.EXAMPLE) {
      problemRepository.setExamples(pid, message.message)
    } else {
      if (message.action == PolygonMessage.END) {
        // Update judge info
        var flag = true
        val version = message.version
        val checkerName =
          if (message.code.containsKey("fullname") && message.code["fullname"] is String) {
            message.code["fullname"] as String
          } else {
            flag = false
            "Unknown"
          }
        val checkerLanguage =
          if (message.code.containsKey("language") && message.code["language"] is String) {
            message.code["language"] as String
          } else {
            flag = false
            "cpp"
          }
        // Uncheck
        val size = message.message.toInt()
        val problem = problemRepository.findProblemById(pid)
        if (flag && problem != null) {
          classicJudgeRepository.setClassicJudge(
            pid,
            message.problem,
            version,
            problem.timeLimit,
            problem.memoryLimit,
            checkerName,
            checkerLanguage,
            size
          )
          problemRepository.setTestcaseNum(pid, size)
        } else {
          message.action = PolygonMessage.ERROR
          message.code = emptyMap()
          message.message = "Unknown Error when setting classic judge"
        }
      }
      if (message.action == PolygonMessage.END || message.action == PolygonMessage.ERROR || message.action == PolygonMessage.COMPILE_ERROR) {
        problemLockService.unlock(pid)
      }
      polygonMessageService.savePolygonMessage(message)
    }
  }
}
