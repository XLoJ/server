package cn.xlor.xloj.listener

import cn.xlor.xloj.utils.MinIOUtils
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

const val ProblemBucketName = "problems"

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class StartedListener(
  private val minIOUtils: MinIOUtils
) : ApplicationListener<ApplicationStartedEvent> {
  override fun onApplicationEvent(event: ApplicationStartedEvent) {
    initMinio()
  }

  fun initMinio() {
    if (!minIOUtils.bucketExists(ProblemBucketName)) {
      minIOUtils.makeBucket(ProblemBucketName)
    }
  }
}
