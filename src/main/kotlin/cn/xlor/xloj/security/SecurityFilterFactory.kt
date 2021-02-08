package cn.xlor.xloj.security

import cn.xlor.xloj.repository.UserRepository
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.servlet.Filter

@Configuration
class SecurityFilterFactory(
  private val jwtService: JWTService,
  private val userRepository: UserRepository
) {
  @Bean
  fun userAuthFilter(): FilterRegistrationBean<Filter> {
    val filterRegistrationBean = FilterRegistrationBean<Filter>()
    filterRegistrationBean.setName("userAuth")
    filterRegistrationBean.order = 1
    filterRegistrationBean.filter = UserAuthFilter(jwtService, userRepository)
    filterRegistrationBean.urlPatterns = listOf("/profile")
    return filterRegistrationBean
  }
}
