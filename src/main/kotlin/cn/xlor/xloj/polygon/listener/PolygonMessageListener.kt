package cn.xlor.xloj.polygon.listener

import cn.xlor.xloj.PolygonMessageQueueName
import cn.xlor.xloj.repository.ProblemRepository
import cn.xlor.xloj.security.ProblemLockService
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class PolygonMessageListener(
  private val problemRepository: ProblemRepository,
  private val polygonMessageService: PolygonMessageService,
  private val problemLockService: ProblemLockService
) {
  @RabbitListener(queuesToDeclare = [Queue(PolygonMessageQueueName)])
  fun handlePolygonMessage(message: PolygonMessage) {
    val pid = message.problem.split('-').first().toLong()
    if (message.action == PolygonMessage.EXAMPLE) {
      problemRepository.updateExamples(pid, message.message)
    } else {
      if (message.action == PolygonMessage.END || message.action == PolygonMessage.ERROR) {
        problemLockService.unlock(pid)
      }
      polygonMessageService.savePolygonMessage(message)
    }
  }
}
