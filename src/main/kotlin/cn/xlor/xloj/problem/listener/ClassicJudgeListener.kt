package cn.xlor.xloj.problem.listener

import cn.xlor.xloj.ClassicJudgeMessageQueueName
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class ClassicJudgeListener {
  @RabbitListener(queuesToDeclare = [Queue(ClassicJudgeMessageQueueName)])
  fun handleJudgeMessage(message: String) {
    println(message)
  }
}
