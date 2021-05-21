package cn.xlor.xloj

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import redis.embedded.RedisServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@TestConfiguration
class EmbeddedRedis {
  @Value("\${spring.redis.port}")
  var redisPort: Int = 0

  var redisServer: RedisServer? = null

  @PostConstruct
  fun startRedis() {
    redisServer = RedisServer(redisPort)
    redisServer!!.start()
  }

  @PreDestroy
  fun stopRedis() {
    redisServer!!.stop()
  }
}
