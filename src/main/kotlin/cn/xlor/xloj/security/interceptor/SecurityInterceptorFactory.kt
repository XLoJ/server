package cn.xlor.xloj.security.interceptor

import cn.xlor.xloj.security.ProblemLockService
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SecurityInterceptorFactory(
  private val problemLockService: ProblemLockService
) : WebMvcConfigurer {
  override fun addInterceptors(registry: InterceptorRegistry) {
    registry
      .addInterceptor(ProblemLockInterceptor(problemLockService))
      .addPathPatterns("/polygon/problem/**")
      .order(5)
  }
}
