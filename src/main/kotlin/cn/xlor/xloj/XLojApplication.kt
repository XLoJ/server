package cn.xlor.xloj

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
@EnableCaching
class XLojApplication {
  @Bean
  fun corsConfigurer(): WebMvcConfigurer? {
    return object : WebMvcConfigurer {
      override fun addCorsMappings(registry: CorsRegistry) {
        registry
          .addMapping("/**")
          .allowedMethods("GET", "POST", "PUT", "DELETE")
          .allowedOrigins("*")
      }
    }
  }
}

fun main(args: Array<String>) {
  runApplication<XLojApplication>(*args)
}
