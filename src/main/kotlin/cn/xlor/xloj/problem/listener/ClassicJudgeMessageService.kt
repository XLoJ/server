package cn.xlor.xloj.problem.listener

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class ClassicJudgeMessageService(
  private val redisTemplate: RedisTemplate<String, Any>
) {
  fun getClassicJudgeMessageKey(submissionId: Long) =
    "submissions/$submissionId"

  fun resetClassicJudgeMessage(submissionId: Long) {
    val key = getClassicJudgeMessageKey(submissionId)
    redisTemplate.delete(key)
  }

  fun saveClassicJudgeMessage(
    submissionId: Long,
    classicJudgeMessage: ClassicJudgeMessage
  ) {
    val key = getClassicJudgeMessageKey(submissionId)
    redisTemplate.opsForSet().add(key, classicJudgeMessage)
  }

  fun findClassicJudgeMessage(submissionId: Long): List<Any> {
    val key = getClassicJudgeMessageKey(submissionId)
    return redisTemplate.opsForSet().members(key)?.toList()?.sortedBy {
      if (it is ClassicJudgeMessage) it.index
      else if (it is HashMap<*, *>) {
        val any = it.getOrDefault("index", 0)
        if (any is Int) {
          any
        } else
          0
      } else 0
    } ?: emptyList()
  }
}
