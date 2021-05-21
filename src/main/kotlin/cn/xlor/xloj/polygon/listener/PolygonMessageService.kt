package cn.xlor.xloj.polygon.listener

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class PolygonMessageService(
  private val redisTemplate: RedisTemplate<String, Any>
) {
  fun getPolygonMessageKey(problemName: String, version: Int) =
    "$problemName:$version"

  fun resetPolygonMessage(problemName: String, version: Int): Boolean {
    val polygonMessageKey = getPolygonMessageKey(problemName, version)
    return redisTemplate.delete(polygonMessageKey)
  }

  fun savePolygonMessage(polygonMessage: PolygonMessage) {
    val polygonMessageKey =
      getPolygonMessageKey(polygonMessage.problem, polygonMessage.version)
    redisTemplate.opsForSet().add(polygonMessageKey, polygonMessage)
  }
}
