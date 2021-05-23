package cn.xlor.xloj.problem.judge

import cn.xlor.xloj.ClassicJudgeQueueName
import cn.xlor.xloj.exception.NotFoundException
import cn.xlor.xloj.model.Submission
import cn.xlor.xloj.repository.ClassicJudgeRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class ClassicJudgeService(
  private val classicJudgeRepository: ClassicJudgeRepository,
  private val rabbitTemplate: RabbitTemplate
) {
  fun runClassicJudge(submission: Submission) {
    val classicJudge =
      classicJudgeRepository.findClassicJudge(submission.problem)
        ?: throw NotFoundException("Classic judge ${submission.problem} 信息未找到")

    val payload = HashMap<String, Any>()

    payload += "id" to submission.id.toString()
    payload += "maxTime" to classicJudge.maxTime
    payload += "maxMemory" to classicJudge.maxMemory
    payload += "problem" to mapOf(
      "name" to classicJudge.problemName,
      "checker" to mapOf(
        "name" to classicJudge.checkerName,
        "lang" to classicJudge.checkerLanguage
      )
    )
    payload += "casesVersion" to classicJudge.version
    payload += "cases" to (1..classicJudge.size).map { "$it.in" }

    payload += "code" to submission.body
    payload += "lang" to submission.language

    rabbitTemplate.convertAndSend(ClassicJudgeQueueName, payload)
  }
}
