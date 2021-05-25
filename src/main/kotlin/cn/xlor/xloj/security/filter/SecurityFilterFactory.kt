package cn.xlor.xloj.security.filter

import cn.xlor.xloj.repository.ContestRepository
import cn.xlor.xloj.repository.ProblemRepository
import cn.xlor.xloj.repository.UserRepository
import cn.xlor.xloj.security.JWTService
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.servlet.Filter

@Configuration
class SecurityFilterFactory(
  private val jwtService: JWTService,
  private val userRepository: UserRepository,
  private val problemRepository: ProblemRepository,
  private val contestRepository: ContestRepository
) {
  @Bean
  fun userAuthFilter(): FilterRegistrationBean<Filter> {
    val filterRegistrationBean = FilterRegistrationBean<Filter>()
    filterRegistrationBean.setName("userAuth")
    filterRegistrationBean.order = 1
    filterRegistrationBean.filter = UserAuthFilter(jwtService, userRepository)
    filterRegistrationBean.urlPatterns =
      listOf("/profile", "/polygon/*", "/contest/create", "/contest/admin/*")
    return filterRegistrationBean
  }

  @Bean
  fun userFilter(): FilterRegistrationBean<Filter> {
    val filterRegistrationBean = FilterRegistrationBean<Filter>()
    filterRegistrationBean.setName("user")
    filterRegistrationBean.order = 2
    filterRegistrationBean.filter = UserFilter(jwtService, userRepository)
    filterRegistrationBean.urlPatterns = listOf("/*")
    return filterRegistrationBean
  }

  @Bean
  fun adminAuthFilter(): FilterRegistrationBean<Filter> {
    val filterRegistrationBean = FilterRegistrationBean<Filter>()
    filterRegistrationBean.setName("adminAuth")
    filterRegistrationBean.order = 2
    filterRegistrationBean.filter = AdminAuthFilter(userRepository)
    filterRegistrationBean.urlPatterns =
      listOf("/contest/create", "/contest/admin/*")
    return filterRegistrationBean
  }

  @Bean
  fun polygonAuthFilter(): FilterRegistrationBean<Filter> {
    val filterRegistrationBean = FilterRegistrationBean<Filter>()
    filterRegistrationBean.setName("polygonAuth")
    filterRegistrationBean.order = 2
    filterRegistrationBean.filter = PolygonAuthFilter(userRepository)
    filterRegistrationBean.urlPatterns = listOf("/polygon/*")
    return filterRegistrationBean
  }

  @Bean
  fun problemAuthFilter(): FilterRegistrationBean<Filter> {
    val filterRegistrationBean = FilterRegistrationBean<Filter>()
    filterRegistrationBean.setName("problemAuth")
    filterRegistrationBean.order = 3
    filterRegistrationBean.filter = ProblemAuthFilter(problemRepository)
    filterRegistrationBean.urlPatterns =
      listOf("/polygon/problem/*", "/polygon/judge/*")
    return filterRegistrationBean
  }

  @Bean
  fun ensureProblemTypeFilter(): FilterRegistrationBean<Filter> {
    val filterRegistrationBean = FilterRegistrationBean<Filter>()
    filterRegistrationBean.setName("classicProblemTypeFilter")
    filterRegistrationBean.order = 4
    filterRegistrationBean.filter = EnsureProblemTypeFilter(
      "classic",
      listOf("checker", "validator", "solution", "generator")
    )
    filterRegistrationBean.urlPatterns =
      listOf("/polygon/problem/*", "/polygon/judge/*")
    return filterRegistrationBean
  }

  @Bean
  fun contestAdminFilter(): FilterRegistrationBean<Filter> {
    val filterRegistrationBean = FilterRegistrationBean<Filter>()
    filterRegistrationBean.setName("contestAdmin")
    filterRegistrationBean.order = 5
    filterRegistrationBean.filter =
      ContestAdminFilter(userRepository, contestRepository)
    filterRegistrationBean.urlPatterns = listOf("/contest/admin/*")
    return filterRegistrationBean
  }
}
