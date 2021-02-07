package cn.xlor.oj.configuration

import com.fasterxml.jackson.databind.Module
import org.ktorm.database.Database
import org.ktorm.jackson.KtormModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

/**
 * See https://github.com/kotlin-orm/ktorm-example-spring-boot
 */
@Configuration
class KtormConfiguration(
  val dataSource: DataSource
) {
  /**
   * Register the [Database] instance as a Spring bean.
   */
  @Bean
  fun database(): Database {
    return Database.connectWithSpringSupport(dataSource)
  }

  /**
   * Register Ktorm's Jackson extension to the Spring's container
   * so that we can serialize/deserialize Ktorm entities.
   */
  @Bean
  fun ktormModule(): Module {
    return KtormModule()
  }
}
