package cn.xlor.xloj.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "minio")
class MinioConfiguration {
  lateinit var url: String

  lateinit var accessKey: String

  lateinit var secretKey: String
}
