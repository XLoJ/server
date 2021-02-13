package cn.xlor.xloj.listener

import cn.xlor.xloj.ProblemBucketName
import cn.xlor.xloj.utils.LoggerDelegate
import cn.xlor.xloj.utils.MinIOUtils
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class StartedListener(
  private val minIOUtils: MinIOUtils
) : ApplicationListener<ApplicationStartedEvent> {
  private val logger by LoggerDelegate()

  override fun onApplicationEvent(event: ApplicationStartedEvent) {
    try {
      initMinio()
    } catch (e: Exception) {
      logger.error("minio init fail!")
    }
  }

  fun initMinio() {
    logger.info("Start init minio...")
    if (!minIOUtils.bucketExists(ProblemBucketName)) {
      minIOUtils.makeBucket(ProblemBucketName)
    }
    logger.info("Init minio OK...")
  }
}
