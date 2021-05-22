package cn.xlor.xloj.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.integration.redis.util.RedisLockRegistry

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
class RedisConfiguration {
  lateinit var host: String

  lateinit var port: String

  lateinit var password: String

  @Bean
  fun redisConnectionFactory(): JedisConnectionFactory {
    val redisStandaloneConfiguration = RedisStandaloneConfiguration(
      host,
      port.toInt()
    )
    redisStandaloneConfiguration.password = RedisPassword.of(password)
    return JedisConnectionFactory(redisStandaloneConfiguration)
  }

  @Bean
  fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
    val redisTemplate = RedisTemplate<String, Any>()
    redisTemplate.setConnectionFactory(redisConnectionFactory)
    redisTemplate.keySerializer = StringRedisSerializer()
    redisTemplate.valueSerializer =
      Jackson2JsonRedisSerializer(Object::class.java)
    redisTemplate.afterPropertiesSet()
    return redisTemplate
  }

  @Bean
  fun redisLockRegistry(redisConnectionFactory: RedisConnectionFactory): RedisLockRegistry {
    return RedisLockRegistry(redisConnectionFactory, "lock")
  }
}
