package cn.xlor.xloj.problem.listener

import cn.xlor.xloj.ClassicJudgeMessageQueueName
import cn.xlor.xloj.model.Submission
import cn.xlor.xloj.repository.SubmissionRepository
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class ClassicJudgeListener(
  private val classicJudgeMessageService: ClassicJudgeMessageService,
  private val submissionRepository: SubmissionRepository
) {
  @RabbitListener(queuesToDeclare = [Queue(ClassicJudgeMessageQueueName)])
  fun handleJudgeMessage(message: ClassicJudgeMessage) {
    val submissionId = message.id
    if (message.verdict == Submission.Compiling) {
      classicJudgeMessageService.resetClassicJudgeMessage(submissionId)
      submissionRepository.setSubmissionStatusAndFrom(
        submissionId,
        Submission.Compiling,
        message.from
      )
    } else if (message.verdict == Submission.Running) {
      submissionRepository.setSubmissionStatus(
        submissionId,
        Submission.Running
      )
    } else if (message.verdict == Submission.CompileError
      || message.verdict == Submission.JudgeError
      || message.verdict == Submission.SystemError
      || message.verdict == Submission.TestCaseError
    ) {
      submissionRepository.setSubmissionStatus(submissionId, message.verdict)
    } else if (message.verdict == Submission.Finished) {
      val submission =
        submissionRepository.findSubmissionById(submissionId)!!
      if (submission.verdict == Submission.Running) {
        submissionRepository.setSubmissionStatus(
          submissionId,
          Submission.Accepted
        )
      }
    } else {
      // Handle classic judge result message
      val submission =
        submissionRepository.findSubmissionById(submissionId)!!
      // Time unit in message is second, here is ms
      val time = maxOf((message.time * 1000).toInt(), submission.time)
      val memory = maxOf(message.memory, submission.memory)
      val pass = message.pass
      if (message.verdict == Submission.Accepted) {
        if (submission.verdict == Submission.Running) {
          submissionRepository.updateRunningSubmission(
            submissionId,
            time,
            memory,
            pass
          )
        }
      } else {
        submissionRepository.updateRunningSubmission(
          submissionId,
          time,
          memory,
          pass,
          message.verdict
        )
      }
    }
    message.time = (message.time * 1000).toInt().toDouble()
    classicJudgeMessageService.saveClassicJudgeMessage(message.id, message)
  }
}
