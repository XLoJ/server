package cn.xlor.xloj.polygon.listener

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class PolygonMessageService(
  private val redisTemplate: RedisTemplate<String, Any>
) {
  fun getPolygonMessageKey(problemName: String, version: Int) =
    "messages/$problemName:$version"

  fun resetPolygonMessage(problemName: String, version: Int): Boolean {
    val polygonMessageKey = getPolygonMessageKey(problemName, version)
    return redisTemplate.delete(polygonMessageKey)
  }

  fun savePolygonMessage(polygonMessage: PolygonMessage) {
    val polygonMessageKey =
      getPolygonMessageKey(polygonMessage.problem, polygonMessage.version)
    redisTemplate.opsForSet().add(polygonMessageKey, polygonMessage)
  }

  fun findPolygonMessage(
    problemName: String,
    version: Int
  ): List<Any> {
    val polygonMessageKey = getPolygonMessageKey(problemName, version)
    val members =
      redisTemplate.opsForSet().members(polygonMessageKey)
    return members?.toList()?.sortedBy {
      if (it is PolygonMessage) it.index
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
