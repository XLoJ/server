package cn.xlor.xloj.polygon.listener

import cn.xlor.xloj.PolygonMessageQueueName
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class PolygonMessageListener(
  private val polygonMessageService: PolygonMessageService
) {
  @RabbitListener(queuesToDeclare = [Queue(PolygonMessageQueueName)])
  fun handlePolygonMessage(message: PolygonMessage) {
    polygonMessageService.savePolygonMessage(message)
  }
}
