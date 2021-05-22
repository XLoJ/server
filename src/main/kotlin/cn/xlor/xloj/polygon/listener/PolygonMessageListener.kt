package cn.xlor.xloj.polygon.listener

import cn.xlor.xloj.PolygonMessageQueueName
import cn.xlor.xloj.repository.ProblemRepository
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class PolygonMessageListener(
  private val problemRepository: ProblemRepository,
  private val polygonMessageService: PolygonMessageService
) {
  @RabbitListener(queuesToDeclare = [Queue(PolygonMessageQueueName)])
  fun handlePolygonMessage(message: PolygonMessage) {
    if (message.action == PolygonMessage.EXAMPLE) {
      val pid = message.problem.split('-').first().toLong()
      problemRepository.updateExamples(pid, message.message)
    } else {
      polygonMessageService.savePolygonMessage(message)
    }
  }
}
