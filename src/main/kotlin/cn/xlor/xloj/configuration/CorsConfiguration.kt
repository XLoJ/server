package cn.xlor.xloj.configuration

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfiguration {
  @Bean
  fun processCorsFilter(): FilterRegistrationBean<CorsFilter> {
    val source = UrlBasedCorsConfigurationSource()
    val config = CorsConfiguration()
    config.allowCredentials = false
    config.addAllowedOrigin("*")
    config.addAllowedHeader("*")
    config.addAllowedMethod("*")
    source.registerCorsConfiguration("/**", config)
    val bean =
      FilterRegistrationBean(CorsFilter(source))
    bean.order = 0
    return bean
  }
}
